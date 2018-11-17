package de.rwth.idsg.steve.web.controller;

import de.rwth.idsg.steve.repository.ChargingProfileRepository;
import de.rwth.idsg.steve.service.ChargePointService12_Client;
import de.rwth.idsg.steve.service.ChargePointService15_Client;
import de.rwth.idsg.steve.service.ChargePointService16_Client;
import de.rwth.idsg.steve.web.dto.ocpp.ChangeConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.ClearChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyEnum;
import de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum;
import de.rwth.idsg.steve.web.dto.ocpp.GetCompositeScheduleParams;
import de.rwth.idsg.steve.web.dto.ocpp.GetConfigurationParams;
import de.rwth.idsg.steve.web.dto.ocpp.SetChargingProfileParams;
import de.rwth.idsg.steve.web.dto.ocpp.TriggerMessageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.R;
import static de.rwth.idsg.steve.web.dto.ocpp.ConfigurationKeyReadWriteEnum.RW;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.03.2018
 */
@Controller
@RequestMapping(value = "/manager/operations/v1.6")
public class Ocpp16Controller extends Ocpp15Controller {

    @Autowired
    @Qualifier("ChargePointService16_Client")
    private ChargePointService16_Client client16;

    @Autowired private ChargingProfileRepository chargingProfileRepository;

    // -------------------------------------------------------------------------
    // Paths
    // -------------------------------------------------------------------------

    private static final String GET_COMPOSITE_PATH = "/GetCompositeSchedule";
    private static final String CLEAR_CHARGING_PATH = "/ClearChargingProfile";
    private static final String SET_CHARGING_PATH = "/SetChargingProfile";
    private static final String TRIGGER_MESSAGE_PATH = "/TriggerMessage";

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    protected ChargePointService16_Client getClient16() {
        return client16;
    }

    @Override
    protected ChargePointService15_Client getClient15() {
        return client16;
    }

    @Override
    protected ChargePointService12_Client getClient12() {
        return client16;
    }

    @Override
    protected void setCommonAttributes(Model model) {
        model.addAttribute("cpList", chargePointHelperService.getChargePointsV16());
        model.addAttribute("opVersion", "v1.6");
    }

    @Override
    protected Map<String, String> getConfigurationKeys(ConfigurationKeyReadWriteEnum confEnum) {
        switch (confEnum) {
            case R:
                return ConfigurationKeyEnum.OCPP_16_MAP_R;
            case RW:
                return ConfigurationKeyEnum.OCPP_16_MAP_RW;
            default:
                return Collections.emptyMap();
        }
    }

    @Override
    protected String getRedirectPath() {
        return "redirect:/manager/operations/v1.6/ChangeAvailability";
    }

    @Override
    protected String getPrefix() {
        return "op16";
    }

    // -------------------------------------------------------------------------
    // Old Http methods with changed logic
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.GET)
    public String getGetConf(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new GetConfigurationParams());
        model.addAttribute("ocppConfKeys", getConfigurationKeys(R));
        return getPrefix() + GET_CONF_PATH;
    }

    @RequestMapping(value = CHANGE_CONF_PATH, method = RequestMethod.GET)
    public String getChangeConf(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new ChangeConfigurationParams());
        model.addAttribute("ocppConfKeys", getConfigurationKeys(RW));
        return getPrefix() + CHANGE_CONF_PATH;
    }

    @RequestMapping(value = GET_CONF_PATH, method = RequestMethod.POST)
    public String postGetConf(@Valid @ModelAttribute(PARAMS) GetConfigurationParams params,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            model.addAttribute("ocppConfKeys", getConfigurationKeys(R));
            return getPrefix() + GET_CONF_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient15().getConfiguration(params);
    }

    // -------------------------------------------------------------------------
    // New Http methods (GET)
    // -------------------------------------------------------------------------

    @RequestMapping(value = GET_COMPOSITE_PATH, method = RequestMethod.GET)
    public String getGetCompositeSchedule(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new GetCompositeScheduleParams());
        return getPrefix() + GET_COMPOSITE_PATH;
    }

    @RequestMapping(value = CLEAR_CHARGING_PATH, method = RequestMethod.GET)
    public String getClearChargingProfile(Model model) {
        setCommonAttributes(model);
        model.addAttribute("profileList", chargingProfileRepository.getBasicInfo());
        model.addAttribute(PARAMS, new ClearChargingProfileParams());
        return getPrefix() + CLEAR_CHARGING_PATH;
    }

    @RequestMapping(value = SET_CHARGING_PATH, method = RequestMethod.GET)
    public String getSetChargingProfile(Model model) {
        setCommonAttributes(model);
        model.addAttribute("profileList", chargingProfileRepository.getBasicInfo());
        model.addAttribute(PARAMS, new SetChargingProfileParams());
        return getPrefix() + SET_CHARGING_PATH;
    }

    @RequestMapping(value = TRIGGER_MESSAGE_PATH, method = RequestMethod.GET)
    public String getTriggerMessage(Model model) {
        setCommonAttributes(model);
        model.addAttribute(PARAMS, new TriggerMessageParams());
        return getPrefix() + TRIGGER_MESSAGE_PATH;
    }

    // -------------------------------------------------------------------------
    // Http methods (POST)
    // -------------------------------------------------------------------------

    @RequestMapping(value = TRIGGER_MESSAGE_PATH, method = RequestMethod.POST)
    public String postTriggerMessage(@Valid @ModelAttribute(PARAMS) TriggerMessageParams params,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + TRIGGER_MESSAGE_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient16().triggerMessage(params);
    }

    @RequestMapping(value = SET_CHARGING_PATH, method = RequestMethod.POST)
    public String postSetChargingProfile(@Valid @ModelAttribute(PARAMS) SetChargingProfileParams params,
                                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + SET_CHARGING_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient16().setChargingProfile(params);
    }

    @RequestMapping(value = CLEAR_CHARGING_PATH, method = RequestMethod.POST)
    public String postClearChargingProfile(@Valid @ModelAttribute(PARAMS) ClearChargingProfileParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + CLEAR_CHARGING_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient16().clearChargingProfile(params);
    }

    @RequestMapping(value = GET_COMPOSITE_PATH, method = RequestMethod.POST)
    public String postGetCompositeSchedule(@Valid @ModelAttribute(PARAMS) GetCompositeScheduleParams params,
                                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            setCommonAttributes(model);
            return getPrefix() + GET_COMPOSITE_PATH;
        }
        return REDIRECT_TASKS_PATH + getClient16().getCompositeSchedule(params);
    }
}
