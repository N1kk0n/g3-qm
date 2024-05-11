package g3.qm.queuemanager.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import g3.qm.queuemanager.dtos.DecisionItem;
import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.TaskItem;
import g3.qm.queuemanager.repositories.DecisionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DecisionCreatorService {
    @Autowired
    private DecisionRepository decisionRepository;

    private final Logger LOGGER = LogManager.getLogger(DecisionCreatorService.class);

    private int                queuePage = 1;  // страница вывода очереди (на случай, если очередь очень большая)
    private List<TaskItem>     queueList;      // список профилей задач
    private List<Device>       deviceList;     // список вычислительных устройств
    private List<DecisionItem> decision;       // финальное решение

    private HashSet<Long>    requestBlackList; // список поставленных заявок в ходе выработки решения
    private HashSet<Integer> deviceBlackList;  // список занимаемых устройств в ходе выработки решения

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
        requestBlackList.clear();
        deviceBlackList.clear();
        decision.clear();
    }

    private void chooseDevices(List<TaskItem> taskProfileList) {
        //идентификатор заявки
        long task_id = taskProfileList.get(0).getTask_id();
        long profile_priority = taskProfileList.get(0).getProfile_priority();
        String program_name = taskProfileList.get(0).getProgram_name();

        //проверка идентификатора заявки на наличие ее в черном списке
        if (requestBlackList.contains(task_id)) {
            return;
        }

        //проверка имеется ли в работе заявка с таким же идентификатором и большим приоритетом
        for (Device device : deviceList) {
            if (device.getTask_id() == task_id &&
                    device.getTask_priority() >= taskProfileList.get(0).getProfile_priority())
                return;
        }

        //список арендуемых устройств для текущей заявки (request_list)
        List<Device> device_order_list = new LinkedList<>();
        //общее количество устройств, которое нужно найти
        int total_device_count = 0;
        for (TaskItem taskItem : taskProfileList)
            total_device_count = total_device_count + taskItem.getDevice_count();

        for (TaskItem taskItem : taskProfileList) {
            boolean is_static = taskItem.isProfile_static();
            //если "поле" заявки статическое
            if (is_static) {
                String static_dev_name = taskItem.getDevice_name();
                boolean device_found = false;
                for (Device device : deviceList) {
                    String device_name = device.getDevice_name();
                    long device_priority = device.getTask_priority();

                    if (device_name.equals(static_dev_name) &&
                        profile_priority > device_priority) {
                        //проверка наличия идентификатора в черном списке и в списке устройств для заявки
                        boolean device_in_black_list = false;
                        boolean device_in_order_list = false;
                        if (deviceBlackList.contains(device.getDevice_id())) {
                            device_in_black_list = true;
                        }
                        for (Device ordered_device : device_order_list) {
                            if (ordered_device.getDevice_id() == device.getDevice_id()) {
                                device_in_order_list = true;
                                break;
                            }
                        }
                        //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
                        if (device_in_order_list || device_in_black_list) {
                            continue;
                        }
                        //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
                        device_order_list.add(device);
                        device_found = true;

                        LOGGER.info("Task ID: " + task_id + ". Profile priority: " + profile_priority + ". Device name (static): " + device_name + ". Device priority (static): " + device_priority);
                    }
                }
                //если устройство для статической части заявки не найдено, выходим
                if (!device_found) {
                    device_order_list.clear();
                    return;
                }
            } else {
                String profile_device_type = taskItem.getDevice_type();
                for (Device device : deviceList) {
                    //получаем идентификатор, тип и приоритет устройства
                    String device_name = device.getDevice_name();
                    long device_priority = device.getTask_priority();
                    String device_type = device.getDevice_type();

                    profile_device_type = profile_device_type.trim();
                    device_type = device_type.trim();

                    if (profile_device_type.equals(device_type) && profile_priority > device_priority) {

                        //проверка наличия идентификатора в черном списке и в списке устройств для заявки
                        boolean device_in_black_list = false;
                        boolean device_in_order_list = false;
                        if (deviceBlackList.contains(device.getDevice_id())) {
                            device_in_black_list = true;
                        }
                        for (Device ordered_device : device_order_list) {
                            if (ordered_device.getDevice_id() == device.getDevice_id()) {
                                device_in_order_list = true;
                                break;
                            }
                        }
                        //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
                        if (device_in_order_list || device_in_black_list) {
                            continue;
                        }
                        //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
                        device_order_list.add(device);

                        LOGGER.info("Task ID: " + task_id + ". Profile device type: [" + profile_device_type + "]. Profile priority: " + profile_priority + ". Device name: " + device_name + ". Device type: [" + device_type + "]. Device priority: " + device_priority);

                        if (device_order_list.size() == total_device_count) {
                            break;
                        }
                    }
                }
            }
        }
        if (device_order_list.size() == total_device_count) {
            for (Device ordered_device : device_order_list) {
                if (ordered_device.getTask_id() != -1) {

                    LOGGER.info("Task ID: " + task_id + ": release devices. Stop task with ID: " + ordered_device.getTask_id() + ". Restart");

                    //если устройства заняты другой заявкой, то освобождаем устройства, заносим считающуюся заявку в очередь, завершаем работу
                    terminateTask(ordered_device.getTask_id());
                    taskProfileList.clear();
                    device_order_list.clear();
                    clearWorkResults();
                    chooseRequest();
                    return;
                }
            }
            if (isTaskWorking(task_id)) {

                LOGGER.info(task_id + ": Request is working on another devices. Restart");

                //если заявка считается на других устройствах, то освобождаем устройства, заносим считающуюся заявку в очередь, завершаем работу
                terminateTask(task_id);
                taskProfileList.clear();
                device_order_list.clear();
                clearWorkResults();
                chooseRequest();
                return;
            }

            for (Device ordered_device : device_order_list) {
                decision.add(new DecisionItem(task_id, ordered_device.getDevice_name(), ordered_device.getManager_name()));
            }

            LOGGER.info("Task ID: " + task_id + ", rent devices. Devices: " + device_order_list + " program: " + program_name);

            //заносим устройства в черный список
            for (Device ordered_device : device_order_list)
                deviceBlackList.add(ordered_device.getDevice_id());
            //заносим заявку в черный список
            requestBlackList.add(task_id);
        }
        device_order_list.clear();
    }

    private void chooseRequest() {
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

        queuePage = queuePage + 1;
        queueList = decisionRepository.getTaskProfileList(queuePage);
        chooseRequest();
    }

    public List<DecisionItem> createDecision() {
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
        requestBlackList = new HashSet<>();
        chooseRequest();

        time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
        LOGGER.info("End: " + time_stamp);

        return decision;
    }
}
