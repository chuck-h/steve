package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.ocpp.task.ClearChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.GetCompositeScheduleTask;
import de.rwth.idsg.steve.ocpp.task.SetChargingProfileTask;
import de.rwth.idsg.steve.ocpp.task.TriggerMessageTask;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @author David Rerimassie <david@rerimassie.nl>
 * @since 13.03.2018
 */
@Slf4j
@Service
@Qualifier("ChargePointService16_Client")
public class ChargePointService16_Client extends ChargePointService15_Client {

    @Autowired private ChargePointService16_InvokerImpl invoker16;

    @Override
    protected OcppVersion getVersion() {
        return OcppVersion.V_16;
    }

    @Override
    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker16;
    }

    @Override
    protected ChargePointService15_Invoker getOcpp15Invoker() {
        return invoker16;
    }

    protected ChargePointService16_Invoker getOcpp16Invoker() {
        return invoker16;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.6
    // -------------------------------------------------------------------------

    public int triggerMessage(TriggerMessageParams params) {
        TriggerMessageTask task = new TriggerMessageTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().triggerMessage(c, task));

        return taskStore.add(task);
    }

    public int getCompositeSchedule(GetCompositeScheduleParams params) {
        GetCompositeScheduleTask task = new GetCompositeScheduleTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp16Invoker().getCompositeSchedule(c, task));

        return taskStore.add(task);
    }

    public int clearChargingProfile(ClearChargingProfileParams params) {
        ClearChargingProfileTask task = new ClearChargingProfileTask(getVersion(), params);

        BackgroundService.with(executorService)
                        .forEach(task.getParams().getChargePointSelectList())
                        .execute(c -> getOcpp16Invoker().clearChargingProfile(c, task));

        return taskStore.add(task);
    }

    public int setChargingProfile(SetChargingProfileParams params) {
        SetChargingProfileTask task = new SetChargingProfileTask(getVersion(), params);

        BackgroundService.with(executorService)
                        .forEach(task.getParams().getChargePointSelectList())
                        .execute(c -> getOcpp16Invoker().setChargingProfile(c, task));

        return taskStore.add(task);
    }
}
