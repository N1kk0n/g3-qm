package g3.qm.queuemanager.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.TaskItem;
import g3.qm.queuemanager.repositories.jdbc.DecisionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionCreatorService {
    @Autowired
    private DecisionRepository decisionRepository;

    private final Logger LOGGER = LogManager.getLogger(DecisionCreatorService.class);

    private int                queuePage;       // страница вывода очереди (на случай, если очередь очень большая)
    private List<TaskItem>     queueList;       // список профилей задач
    private List<Device>       deviceList;      // список вычислительных устройств
    private List<DecisionItem> decision;        // финальное решение

    private HashSet<Long>      taskBlackList;   // список поставленных заявок в ходе выработки решения
    private HashSet<Integer>   deviceBlackList; // список занимаемых устройств в ходе выработки решения

    //освобождение устройств
    private void terminateTask(long taskId) {
        //обнуляем статус
        for (TaskItem taskProfile : queueList)
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
        for (TaskItem taskItem : queueList)
            if (taskItem.getTask_id() == taskId && taskItem.getProfile_status().equals("IN_WORK")) {
                is_working = true;
                break;
            }
        return is_working;
    }

    //служебная: очистка памяти
    private void clearWorkResults() {
        taskBlackList.clear();
        deviceBlackList.clear();
        decision.clear();
    }

    private void chooseDevices(List<TaskItem> taskProfileList) {
        //идентификатор заявки
        long taskId = taskProfileList.get(0).getTask_id();
        long profilePriority = taskProfileList.get(0).getProfile_priority();
        String programName = taskProfileList.get(0).getProgram_name();

        //проверка идентификатора заявки на наличие ее в черном списке
        if (taskBlackList.contains(taskId)) {
            return;
        }

        //проверка имеется ли в работе заявка с таким же идентификатором и большим приоритетом
        for (Device device : deviceList) {
            if (device.getTask_id() == taskId &&
                    device.getTask_priority() >= taskProfileList.get(0).getProfile_priority())
                return;
        }

        //список арендуемых устройств для текущей заявки (request_list)
        List<Device> deviceOrderList = new LinkedList<>();
        //общее количество устройств, которое нужно найти
        int totalDeviceCount = 0;
        for (TaskItem taskItem : taskProfileList)
            totalDeviceCount = totalDeviceCount + taskItem.getDevice_count();

        for (TaskItem taskItem : taskProfileList) {
            boolean isStatic = taskItem.isProfile_static();
            //если "поле" заявки статическое
            if (isStatic) {
                String static_dev_name = taskItem.getDevice_name();
                boolean device_found = false;
                for (Device device : deviceList) {
                    String deviceName = device.getDevice_name();
                    long devicePriority = device.getTask_priority();

                    if (deviceName.equals(static_dev_name) &&
                        profilePriority > devicePriority) {
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

                        LOGGER.info("Task ID: " + taskId + ". Profile priority: " + profilePriority + ". Device name (static): " + deviceName + ". Device priority (static): " + devicePriority);
                    }
                }
                //если устройство для статической части заявки не найдено, выходим
                if (!device_found) {
                    deviceOrderList.clear();
                    return;
                }
            } else {
                String profileDeviceType = taskItem.getDevice_type();
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

                        LOGGER.info("Task ID: " + taskId + ". Profile device type: [" + profileDeviceType + "]. Profile priority: " + profilePriority + ". Device name: " + deviceName + ". Device type: [" + deviceType + "]. Device priority: " + devicePriority);

                        if (deviceOrderList.size() == totalDeviceCount) {
                            break;
                        }
                    }
                }
            }
        }
        if (deviceOrderList.size() == totalDeviceCount) {
            for (Device ordered_device : deviceOrderList) {
                if (ordered_device.getTask_id() != -1) {

                    LOGGER.info("Task ID: " + taskId + ". Release devices. Stop task with ID: " + ordered_device.getTask_id() + ". Restart");

                    //если устройства заняты другой заявкой, то освобождаем устройства, заносим считающуюся заявку в очередь, перезапускаем алгоритм
                    terminateTask(ordered_device.getTask_id());
                    taskProfileList.clear();
                    deviceOrderList.clear();
                    clearWorkResults();
                    chooseTask();
                    return;
                }
            }
            if (isTaskWorking(taskId)) {

                LOGGER.info("Task ID: " + taskId + ". Task is working on another devices. Restart");

                //если заявка считается на других устройствах, то освобождаем устройства, заносим считающуюся заявку в очередь, перезапускаем алгоритм
                terminateTask(taskId);
                taskProfileList.clear();
                deviceOrderList.clear();
                clearWorkResults();
                chooseTask();
                return;
            }

            for (Device ordered_device : deviceOrderList) {
                decision.add(new DecisionItem(taskId, ordered_device.getDevice_name(), ordered_device.getManager_name()));
            }

            LOGGER.info("Task ID: " + taskId + ". Rent devices. Devices: " + deviceOrderList + " program: " + programName);

            //заносим устройства и заявку в черные списки
            for (Device ordered_device : deviceOrderList)
                deviceBlackList.add(ordered_device.getDevice_id());
            taskBlackList.add(taskId);
        }
        deviceOrderList.clear();
    }

    private void chooseTask() {
        if (deviceList.size() == deviceBlackList.size()) {
            LOGGER.info("Size of device list equals black list size. Exit");
            return;
        }

        List<TaskItem> taskProfilesList = new LinkedList<>();

        for (TaskItem taskItem : queueList) {
            if (taskProfilesList.isEmpty()) {
                if (taskItem.getProfile_status().equals("IN_QUEUE"))
                    taskProfilesList.add(taskItem);
                continue;
            }
            if (taskItem.getTask_id() == taskProfilesList.get(taskProfilesList.size() - 1).getTask_id() &&
                taskItem.getProfile_name().equals(taskProfilesList.get(taskProfilesList.size() - 1).getProfile_name()) &&
                taskItem.getProfile_status().equals("IN_QUEUE")) {
                taskProfilesList.add(taskItem);
            } else {
                chooseDevices(taskProfilesList);

                taskProfilesList.clear();
                taskProfilesList.add(taskItem);
            }
        }
        //Если заявки в списке
        if (!taskProfilesList.isEmpty()){
            chooseDevices(taskProfilesList);
        }

        //Получаем следующую страницу очереди, продолжаем выбирать задачи
        queuePage = queuePage + 1;
        queueList = decisionRepository.getTaskProfileList(queuePage);
        chooseTask();
    }

    public List<DecisionItem> createDecision() {
        queuePage = 1;
        decision = new LinkedList<>();

        String time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
        LOGGER.info("Begin: " + time_stamp);

        queueList = decisionRepository.getTaskProfileList(queuePage);
        if (queueList.isEmpty()){
            LOGGER.info("Queue list is empty");
            time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
            LOGGER.info("End: " + time_stamp);
            return decision;
        }

        deviceList = decisionRepository.getDeviceList();
        if (deviceList.isEmpty()){
            LOGGER.info("Device list is empty");
            time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
            LOGGER.info("End: " + time_stamp);
            return decision;
        }

        //инициализация черных списков для сессии
        deviceBlackList = new HashSet<>();
        taskBlackList = new HashSet<>();
        chooseTask();

        time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
        LOGGER.info("End: " + time_stamp);

        return decision;
    }
}
