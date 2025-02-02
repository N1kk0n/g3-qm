package g3.qm.queuemanager.services;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.TaskProfile;
import g3.qm.queuemanager.repositories.state.DecisionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
public class DecisionCreatorService {

    private final DecisionRepository decisionRepository;

    public DecisionCreatorService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    private final Logger LOGGER = LogManager.getLogger(DecisionCreatorService.class);

    private List<TaskProfile> queueList;         // список профилей задач
    private List<Device> deviceList;          // список вычислительных устройств
    private List<DecisionItem> decision;      // финальное решение

    private HashSet<Long> taskBlackList;      // список поставленных заявок в ходе выработки решения
    private HashSet<Integer> deviceBlackList; // список занимаемых устройств в ходе выработки решения

    //освобождение устройств
    private void terminateTask(long taskId) {
        //обнуляем статус
        for (TaskProfile taskProfile : queueList)
            if (taskProfile.getTask_id() == taskId && taskProfile.getProfile_status().equals("IN_WORK"))
                taskProfile.setProfile_status("IN_QUEUE");
        //убираем признаки занятого устройства
        for (Device device : deviceList) {
            if (device.getTask_id() == taskId) {
                device.setTask_id(-1);
                device.setTask_priority(0);
            }
        }
    }

    //проверка заявки на состояние счета
    private boolean isTaskWorking(long taskId) {
        boolean is_working = false;
        for (TaskProfile taskProfile : queueList)
            if (taskProfile.getTask_id() == taskId && taskProfile.getProfile_status().equals("IN_WORK")) {
                is_working = true;
                break;
            }
        return is_working;
    }

    //сброс результатов перед рестартом алгоритма
    private void clearWorkResults() {
        taskBlackList.clear();
        deviceBlackList.clear();
        decision.clear();
    }

    private void finishClean() {
        taskBlackList.clear();
        deviceList.clear();
        queueList.clear();
        deviceList.clear();
    }

    private boolean chooseDevices(TaskProfile taskProfile) {
        //идентификатор заявки
        long taskId = taskProfile.getTask_id();
        int programId = taskProfile.getProgram_id();
        long profilePriority = taskProfile.getProfile_priority();

        //проверка идентификатора заявки на наличие ее в черном списке
        if (taskBlackList.contains(taskId)) {
            return false;
        }

        //проверка имеется ли в работе заявка с таким же идентификатором и большим приоритетом
        for (Device device : deviceList) {
            if (device.getTask_id() == taskId && device.getTask_priority() >= taskProfile.getProfile_priority())
                return false;
        }

        //список арендуемых устройств для текущей заявки (request_list)
        List<Device> deviceOrderList = new LinkedList<>();
        //общее количество устройств, которое нужно найти
        int totalDeviceCount = taskProfile.getDevice_count();

        boolean isStatic = taskProfile.isProfile_static();
        //если "поле" заявки статическое
        if (isStatic) {
            String static_dev_name = taskProfile.getDevice_name();
            boolean device_found = false;
            for (Device device : deviceList) {
                String deviceName = device.getDevice_name();
                long devicePriority = device.getTask_priority();

                if (deviceName.equals(static_dev_name) && profilePriority > devicePriority) {
                    //проверка наличия идентификатора в черном списке и в списке устройств для заявки
                    boolean deviceInBlackList = false;
                    boolean deviceInOrderList = false;
                    if (deviceBlackList.contains(device.getDevice_id())) {
                        deviceInBlackList = true;
                    }
                    for (Device ordered_device : deviceOrderList) {
                        if (ordered_device.getDevice_id() == device.getDevice_id()) {
                            deviceInOrderList = true;
                            break;
                        }
                    }
                    //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
                    if (deviceInOrderList || deviceInBlackList) {
                        continue;
                    }
                    //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
                    deviceOrderList.add(device);
                    device_found = true;

                    LOGGER.info("Task ID: " + taskId + ". Program ID: " + programId + ". Profile priority: " + profilePriority + ". Device name (static): " + deviceName + ". Device priority (static): " + devicePriority);
                }
            }
            //если устройство для статической части заявки не найдено, выходим
            if (!device_found) {
                return false;
            }
        } else {
            String profileDeviceType = taskProfile.getDevice_type();
            for (Device device : deviceList) {
                //получаем идентификатор, тип и приоритет устройства
                String deviceName = device.getDevice_name();
                long devicePriority = device.getTask_priority();
                String deviceType = device.getDevice_type();

                profileDeviceType = profileDeviceType.trim();
                deviceType = deviceType.trim();

                if (profileDeviceType.equals(deviceType) && profilePriority > devicePriority) {

                    //проверка наличия идентификатора в черном списке и в списке устройств для заявки
                    boolean deviceInBlackList = false;
                    boolean deviceInOrderList = false;
                    if (deviceBlackList.contains(device.getDevice_id())) {
                        deviceInBlackList = true;
                    }
                    for (Device ordered_device : deviceOrderList) {
                        if (ordered_device.getDevice_id() == device.getDevice_id()) {
                            deviceInOrderList = true;
                            break;
                        }
                    }
                    //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
                    if (deviceInOrderList || deviceInBlackList) {
                        continue;
                    }
                    //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
                    deviceOrderList.add(device);

                    LOGGER.info("Task ID: " + taskId + ". Program ID: " + programId + ". Profile device type: [" + profileDeviceType + "]. Profile priority: " + profilePriority + ". Device name: " + deviceName + ". Device type: [" + deviceType + "]. Device priority: " + devicePriority);

                    if (deviceOrderList.size() == totalDeviceCount) {
                        break;
                    }
                }
            }
        }

        if (deviceOrderList.size() == totalDeviceCount) {
            for (Device ordered_device : deviceOrderList) {
                if (ordered_device.getTask_id() != -1) {
                    LOGGER.info("Task ID: " + taskId + ". Release devices. Stop task with ID: " + ordered_device.getTask_id() + ". Restart");

                    //если устройства заняты другой заявкой, то освобождаем устройства, перезапускаем алгоритм
                    terminateTask(ordered_device.getTask_id());
                    clearWorkResults();
                    deviceOrderList.clear();
                    return true;
                }
            }
            if (isTaskWorking(taskId)) {
                LOGGER.info("Task ID: " + taskId + ". Task is working on another devices. Restart");

                //если заявка считается на других устройствах, то освобождаем устройства, перезапускаем алгоритм
                terminateTask(taskId);
                clearWorkResults();
                deviceOrderList.clear();
                return true;
            }

            //добавляем устройства в решение, заносим устройства и заявку в черные списки
            for (Device ordered_device : deviceOrderList) {
                deviceBlackList.add(ordered_device.getDevice_id());
            }
            taskBlackList.add(taskId);

            decision.add(new DecisionItem(taskId, programId, deviceOrderList));
            LOGGER.info("Task ID: " + taskId + ". Program ID: " + programId + ". Rent devices. Devices: " + deviceOrderList);
        }
        deviceOrderList.clear();
        return false;
    }

    public List<DecisionItem> createDecision() {
        decision = new LinkedList<>();

        String time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
        LOGGER.info("Begin: " + time_stamp);

        deviceList = decisionRepository.getDeviceList();
        if (deviceList.isEmpty()) {
            LOGGER.info("Device list is empty");
            time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
            LOGGER.info("End: " + time_stamp);
            return decision;
        }

        //инициализация черных списков для сессии
        deviceBlackList = new HashSet<>();
        taskBlackList = new HashSet<>();

        boolean restart = false;
        for (int queuePage = 1; queuePage < Integer.MAX_VALUE; queuePage++) {
            if (!restart) {
                queueList = decisionRepository.getTaskProfileList(queuePage);
            }

            if (queueList.isEmpty()) {
                LOGGER.info("Queue list is empty");
                time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
                LOGGER.info("End: " + time_stamp);

                finishClean();
                return decision;
            }

            for (TaskProfile taskProfile : queueList) {
                restart = chooseDevices(taskProfile);
                if (restart) {
                    queuePage = 0;
                    break;
                }
                if (deviceList.size() == deviceBlackList.size()) {
                    LOGGER.info("Size of device list equals black list size. Exit");
                    time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
                    LOGGER.info("End: " + time_stamp);

                    finishClean();
                    return decision;
                }
            }
        }

        time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
        LOGGER.info("End: " + time_stamp);

        finishClean();
        return decision;
    }
}
