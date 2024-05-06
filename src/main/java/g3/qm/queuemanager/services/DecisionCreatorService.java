package g3.qm.queuemanager.services;

import java.util.HashSet;
import java.util.List;

import g3.qm.queuemanager.dtos.Device;
import g3.qm.queuemanager.dtos.TaskItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DecisionCreatorService {
    private boolean debugMode = false;
    private final Logger LOGGER = LogManager.getLogger(DecisionInfoService.class);

    private List<TaskItem> queueList;         //список профилей задач
    private List<Device> deviceList;          //список вычислительных устройств
    private HashSet<Long> immuneTaskList;     //список задач которые нельзя снимать со счета (в процессе инициализации, почти досчитанные)

    private HashSet<Long> requestBlackList;   //список поставленных заявок в ходе выработки решения
    private HashSet<Integer> deviceBlackList; //список занимаемых устройств в ходе выработки решения

    private boolean checkDebugMode() {
        return true;
    }

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
        //TODO: clear table DECISION
    }
//
//    public void chooseDevices(List<TaskItem> taskProfileList) {
//        //идентификатор заявки
//        long request_id = taskProfileList.get(0).getTask_id();
//        long request_priority = taskProfileList.get(0).getProfile_priority();
//        int program_id = taskProfileList.get(0).getProgram_id();
//        int profile_id = taskProfileList.get(0).get();
//
//        //проверка заявки на наличие ее в списке задач, которые нельзя вытеснять
//        if (immuneTaskList.contains(request_id)) {
//            return;
//        }
//
//        //проверка идентификатора заявки на наличие ее в черном списке
//        if (requestBlackList.contains(request_id)) {
//            return;
//        }
//
//        //проверка имеется ли в работе заявка с таким же идентификатором и большим приоритетом
//        for (int i = 0; i < deviceList.size(); i++) {
//            if (deviceList.get(i).getTaskId() == request_id &&
//                    deviceList.get(i).getPriority() >= taskProfileList.get(0).getPriority())
//                return;
//        }
//
//        //список арендуемых устройств для текущей заявки (request_list)
//        List<LogicalComputingDevice> device_order_list = new ArrayList<>();
//        //общее количество устройств, которое нужно найти
//        int total_device_count = 0;
//        for (int i = 0; i < taskProfileList.size(); i++)
//            total_device_count = total_device_count + taskProfileList.get(i).getLvuCount();
//
//        for (int i = 0; i < taskProfileList.size(); i++) {
//            boolean is_static = taskProfileList.get(i).getIsStatic();
//            //если "поле" заявки статическое
//            if (is_static){
//                int static_dev_id = taskProfileList.get(i).getStaticLvuId();
//                boolean device_found = false;
//                for (int k = 0; k < deviceList.size(); k++){
//                    int device_id = deviceList.get(k).getLvuId();
//                    long device_priority = deviceList.get(k).getPriority();
//
//                    if (device_id == static_dev_id && request_priority > device_priority){
//                        //проверка наличия идентификатора в черном списке и в списке устройств для заявки
//                        boolean device_in_black_list = false;
//                        boolean device_in_order_list = false;
//                        if (deviceBlackList.contains(device_id)) {
//                            device_in_black_list = true;
//                        }
//                        for (int l = 0; l < device_order_list.size(); l++) {
//                            if (device_order_list.get(l).getLvuId() == device_id) {
//                                device_in_order_list = true;
//                            }
//                        }
//                        //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
//                        if (device_in_order_list || device_in_black_list) {
//                            continue;
//                        }
//                        //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
//                        device_order_list.add(deviceList.get(k));
//                        device_found = true;
//
//                        if (debugMode) LOGGER.debug("Request ID: " + request_id + " " + "Request priority: " + request_priority + " " + "Device ID (static): " + device_id + " " + "Device priority (static): " + device_priority);
//                    }
//                }
//                //если устройство для статической части заявки не найдено, выходим
//                if (!device_found) {
//                    device_order_list.clear();
//                    return;
//                }
//            } else {
//                String request_type = taskProfileList.get(i).getType();
//                for (int k = 0; k < deviceList.size(); k++) {
//                    //получаем идентификатор, тип и приоритет устройства
//                    int device_id = deviceList.get(k).getLvuId();
//                    long device_priority = deviceList.get(k).getPriority();
//                    String device_type = deviceList.get(k).getType();
//
//                    request_type = request_type.trim();
//                    device_type = device_type.trim();
//
//                    if (request_type.equals(device_type) && request_priority > device_priority) {
//
//                        //проверка наличия идентификатора в черном списке и в списке устройств для заявки
//                        boolean device_in_black_list = false;
//                        boolean device_in_order_list = false;
//                        if (deviceBlackList.contains(device_id)) {
//                            device_in_black_list = true;
//                        }
//                        for (int l = 0; l < device_order_list.size(); l++) {
//                            if (device_order_list.get(l).getLvuId() == device_id) {
//                                device_in_order_list = true;
//                            }
//                        }
//                        //если устройство с текущим идентификатором уже арендовано, то продолжаем поиск
//                        if (device_in_order_list || device_in_black_list ) {
//                            continue;
//                        }
//                        //если устройство с текущим идентификатором не арендовано, то добавляем его в список арендуемых устройств
//                        device_order_list.add(deviceList.get(k));
//
//                        if (debugMode) LOGGER.debug("Request ID: " + request_id + " " + "Request type: [" + request_type + "] " + "Request priority: " + request_priority + " " + "Device ID: " + device_id + " " + "Device type: [" + device_type + "] " + "Device priority: " + device_priority);
//
//                        if (device_order_list.size() == total_device_count) {
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        if (device_order_list.size() == total_device_count) {
//            for (int i = 0; i < total_device_count; i++) {
//                if (device_order_list.get(i).getTaskId() != -1) {
//
//                    if (debugMode) LOGGER.debug("Request ID: " + request_id + ": release devices. Stop task with id: " + device_order_list.get(i).getTaskId() + ". Restart");
//
//                    //если устройства заняты другой заявкой, то освоборждаем устройства, заносим считающуюся заявку в очередь, завершаем работу
//                    terminateTask(device_order_list.get(i).getTaskId());
//                    taskProfileList.clear();
//                    device_order_list.clear();
//                    clearWorkResults();
//                    chooseRequest();
//                    return;
//                }
//            }
//            if (isTaskWorking(request_id)) {
//
//                if (debugMode) LOGGER.debug(request_id + ": Request is working on another devices. Restart");
//
//                //если заявка считается на других устройствах, то освоборждаем устройства, заносим считающуюся заявку в очередь, завершаем работу
//                terminateTask(request_id);
//                taskProfileList.clear();
//                device_order_list.clear();
//                clearWorkResults();
//                chooseRequest();
//                return;
//            }
//            work_decision.add(new WorkRequest(request_id, program_id, profile_id, request_priority, device_order_list));
//
//            if (debugMode) LOGGER.debug("Request ID: " + request_id + ", rent devices. Devices: " + device_order_list.toString() + request_id + " program: " + program_id + " profile: " + profile_id);
//
//            //заносим устройства в черный список
//            for (int i = 0; i < total_device_count; i++)
//                deviceBlackList.add(device_order_list.get(i).getLvuId());
//            //заносим заявку в черный список
//            requestBlackList.add(request_id);
//        }
//        device_order_list.clear();
//    }
//
//    public void chooseRequest() {
//        if (deviceList.size() == deviceBlackList.size()) {
//            if (debugMode) LOGGER.debug("QManagerSessionBean.CreateRentRequest: Size of device list equals black list size. Exit");
//            return;
//        }
//
//        // TODO [ DELETE? ]: помещаем в черный список задачи которые нельзя снимать
//        // request_black_list.addAll(immune_tasks_list);
//
//        List<WorkRequestField> request_list = new ArrayList<>();
//
//        for (int i = 0; i < queueList.size(); i++) {
//            if (request_list.isEmpty()) {
//                if (queueList.get(i).getStatus() == 0)
//                    request_list.add(queueList.get(i));
//                continue;
//            }
//            if (queueList.get(i).getId() == request_list.get(request_list.size() - 1).getId() &&
//                    queueList.get(i).getProfileId() == request_list.get(request_list.size() - 1).getProfileId() &&
//                    queueList.get(i).getStatus() == 0) {
//                request_list.add(queueList.get(i));
//            } else {
//                chooseDevices(request_list);
//
//                request_list.clear();
//                request_list.add(queueList.get(i));
//            }
//        }
//        //Если заявки в списке
//        if (!request_list.isEmpty()){
//            chooseDevices(request_list);
//        }
//    }
//
//    @Override
//    public List<WorkRequest> createNewDecision() throws SQLException {
//        debugMode = checkDebugMode();
//
//        String time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
//        LOGGER.info("Begin: " + time_stamp);
//
//        queueList = QueueUtils.selectQueue();
//        if (queueList.isEmpty()){
//            LOGGER.info("Queue list is empty");
//            time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
//            LOGGER.debug("End: " + time_stamp);
//            return null;
//        }
//
//        immuneTaskList = QueueUtils.selectImmuneTasks();
//        if (immuneTaskList.isEmpty()){
//            LOGGER.info("Immune task list is empty");
//        } else {
//            LOGGER.info("Immune task list:");
//            for (int i = 0; i < immuneTaskList.size(); i++)
//                LOGGER.info(immuneTaskList.get(i).toString());
//        }
//
//        deviceList = LogicalComputingDevice.selectDevices();
//        if (deviceList.isEmpty()){
//            LOGGER.info("Device list is empty");
//            time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
//            LOGGER.info("End: " + time_stamp);
//            return null;
//        }
//
//        //инициализация черных списков для сессии
//        deviceBlackList = new ArrayList<>();
//        requestBlackList = new ArrayList<>();
//        work_decision = new ArrayList<>();
//        chooseRequest();
//
//        for (int i = 0; i < work_decision.size(); i++) {
//            LOGGER.info(work_decision.get(i).toString());
//        }
//        time_stamp = new SimpleDateFormat("dd/MM/yy HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
//        LOGGER.info("Size of decision list: " + work_decision.size());
//        LOGGER.info("End: " + time_stamp);
//
//        return work_decision;
//    }
}
