package com.honda.mdve.sampleapplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VehcleDataDef {
    public static final String[]
            mVehcledataAPIList = new String[]{
            "ACCELPEDAL",
            "ENGINERPM",
            "ENGINESTAT",
            "ENGOILLIFE",
            "SHIFT",
            "CRUISE",
            "METERSPEED",
            "TMSPEED",
            "ODOMETER",
            "TRAVELLEDDIST",
            "FUEL",
            "FUELECONOMY",
            "TRIP",
            "ESTIMATEDRANGE",
            "STEER",
            "BRAKE",
            "PARKINGBRAKE",
            "TIREPRESSURE",
            "VSA",
            "WHEELSPEED",
            "VEHICLEMOVE",
            "VIN",
            "BATVOLTAGE",
            "IG",
            "ILLSTAT",
            "BACKLIGHTSTAT",
            "TURNSIGNAL",
            "HEADLIGHT",
            "HAZARD",
            "OUTSIDETEMP",
            "ACSETTEMP",
            "ACCURRENTTEMP",
            "RAIN",
            "WIPER",
            "WINDOWCLOSE",
            "PASSENGEREXISTENCE",
            "TIMESTAMP",
            "HEADUNITINFO",
            "CARDIRECTION",
            "CARCONDITION",
            "SUNROOF",
            "DOORTYPE",
            "WINDOWSTATUS",
            "NAVIROADATTR",
            "NAVIONROAD",
            "MODE",
            "SEATBELT",
            "ACFANSPEED",
            "ACMODE",
            "ACSYNC",
            "ACRECFRE",
            "ACAC",
            "ACDEFRR",
            "ACDEFFR",
            "ACVARIATION",
            "ACLHRH",
            "ACTEMPSTEP",
            "ACMODERR",
            "ACFANSPEEDRR",
            "ACSETTEMPRR",
            "CLUTCHSW",
            "ENGMANVSENSV",
            "BRAKESW",
            "VEHICLESTJUDGE",
            "CLUTCHSWSIL",
            "ATGEARPOS",
            "SMATICSHIFT",
            "CRANKSHAFT",
            "PRECEDINGVEHICLE",
            "MTGEARPOS",
            "REVMATCH",
            "DAASINFO",
            "ADASINFO",
            "CRUISESW",
            "DISPLAYSETTINGGRAPHIC",
            "DISPLAYSETTINGTV",
            "DISPLAYSETTINGCOMPRESSVIDEO",
            "DISPLAYSETTINGAUXVIDEO",
            "DISPLAYSETTINGHDMI",
            "DISPLAYSETTINGDVD",
            "DISPLAYSETTINGREARWIDECAMERA",
            "DISPLAYSETTINGMVC",
            "DISPLAYSETTINGLANEWATCH",
            "VOLUMESETTING",
            "TOUCHPANEL",
            "TONE",
            "FADERBALANCE",
            "SUBWOOFER",
            "SVC",
            "CURRENTAUDIO"
    };

    public static final int ItemID_accel_pedal_position             =  0;
    public static final int ItemID_engine                           =  1;
    public static final int ItemID_atmospheric_pressure             =  2;
    public static final int ItemID_intake_air_temperature           =  3;
    public static final int ItemID_engine_coolant_temperature       =  4;
    public static final int ItemID_cruise_set_speed                 =  5;
    public static final int ItemID_vehicle_speed                    =  6;
    public static final int ItemID_transmission_speed               =  7;
    public static final int ItemID_odometer                         =  8;
    public static final int ItemID_traveled_distance                =  9;
    public static final int ItemID_accumulated_fuel_consumption     = 10;
    public static final int ItemID_remained_fuel                    = 11;
    public static final int ItemID_instantaneous_fuel_economy       = 12;
    public static final int ItemID_fuel_economy_max_unit            = 13;
    public static final int ItemID_average_fuel_economy_a           = 14;
    public static final int ItemID_average_fuel_economy_b           = 15;
    public static final int ItemID_fuel_economy_unit                = 16;
    public static final int ItemID_trip_a                           = 17;
    public static final int ItemID_trip_b                           = 18;
    public static final int ItemID_estimated_range                  = 19;
    public static final int ItemID_estimated_range_unit             = 20;
    public static final int ItemID_steering_angle                   = 21;
    public static final int ItemID_steering_angle_velocity          = 22;
    public static final int ItemID_brake_pressure                   = 23;
    public static final int ItemID_tire_pressure_fr                 = 24;
    public static final int ItemID_tire_pressure_fl                 = 25;
    public static final int ItemID_tire_pressure_rr                 = 26;
    public static final int ItemID_tire_pressure_rl                 = 27;
    public static final int ItemID_wheel_speed_fl                   = 28;
    public static final int ItemID_wheel_speed_fr                   = 29;
    public static final int ItemID_wheel_speed_rl                   = 30;
    public static final int ItemID_wheel_speed_rr                   = 31;
    public static final int ItemID_longitudinal_g                   = 32;
    public static final int ItemID_lateral_g                        = 33;
    public static final int ItemID_yaw_rate                         = 34;
    public static final int ItemID_b_voltage                        = 35;
    public static final int ItemID_ac_target_temp_dr                = 36;
    public static final int ItemID_ac_target_temp_as                = 37;
    public static final int ItemID_outside_temperature              = 38;
    public static final int ItemID_ac_current_temperature           = 39;
    public static final int ItemID_timestamp                        = 40;
    public static final int ItemID_trip_a_unit                      = 41;
    public static final int ItemID_trip_b_unit                      = 42;
    public static final int ItemID_max                              = 43;

    public static final Map<String, Integer> mVehcledataTag_StringToInt;
    static {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("accel_pedal_position"           , ItemID_accel_pedal_position            );
        map.put("engine"                         , ItemID_engine                          );
        map.put("atmospheric_pressure"           , ItemID_atmospheric_pressure            );
        map.put("intake_air_temperature"         , ItemID_intake_air_temperature          );
        map.put("engine_coolant_temperature"     , ItemID_engine_coolant_temperature      );
        map.put("cruise_set_speed"               , ItemID_cruise_set_speed                );
        map.put("vehicle_speed"                  , ItemID_vehicle_speed                   );
        map.put("transmission_speed"             , ItemID_transmission_speed              );
        map.put("odometer"                       , ItemID_odometer                        );
        map.put("traveled_distance"              , ItemID_traveled_distance               );
        map.put("accumulated_fuel_consumption"   , ItemID_accumulated_fuel_consumption    );
        map.put("remained_fuel"                  , ItemID_remained_fuel                   );
        map.put("instantaneous_fuel_economy"     , ItemID_instantaneous_fuel_economy      );
        map.put("fuel_economy_max_unit"          , ItemID_fuel_economy_max_unit           );
        map.put("average_fuel_economy_a"         , ItemID_average_fuel_economy_a          );
        map.put("average_fuel_economy_b"         , ItemID_average_fuel_economy_b          );
        map.put("fuel_economy_unit"              , ItemID_fuel_economy_unit               );
        map.put("trip_a"                         , ItemID_trip_a                          );
        map.put("trip_b"                         , ItemID_trip_b                          );
        map.put("estimated_range"                , ItemID_estimated_range                 );
        map.put("estimated_range_unit"           , ItemID_estimated_range_unit            );
        map.put("steering_angle"                 , ItemID_steering_angle                  );
        map.put("steering_angle_velocity"        , ItemID_steering_angle_velocity         );
        map.put("brake_pressure"                 , ItemID_brake_pressure                  );
        map.put("tire_pressure_fr"               , ItemID_tire_pressure_fr                );
        map.put("tire_pressure_fl"               , ItemID_tire_pressure_fl                );
        map.put("tire_pressure_rr"               , ItemID_tire_pressure_rr                );
        map.put("tire_pressure_rl"               , ItemID_tire_pressure_rl                );
        map.put("wheel_speed_fl"                 , ItemID_wheel_speed_fl                  );
        map.put("wheel_speed_fr"                 , ItemID_wheel_speed_fr                  );
        map.put("wheel_speed_rl"                 , ItemID_wheel_speed_rl                  );
        map.put("wheel_speed_rr"                 , ItemID_wheel_speed_rr                  );
        map.put("longitudinal_g"                 , ItemID_longitudinal_g                  );
        map.put("lateral_g"                      , ItemID_lateral_g                       );
        map.put("yaw_rate"                       , ItemID_yaw_rate                        );
        map.put("b_voltage"                      , ItemID_b_voltage                       );
        map.put("ac_target_temp_dr"              , ItemID_ac_target_temp_dr               );
        map.put("ac_target_temp_as"              , ItemID_ac_target_temp_as               );
        map.put("outside_temperature"            , ItemID_outside_temperature             );
        map.put("ac_current_temperature"         , ItemID_ac_current_temperature          );
        map.put("timestamp"                      , ItemID_timestamp                       );
        map.put("trip_a_unit"                    , ItemID_trip_a_unit                     );
        map.put("trip_b_unit"                    , ItemID_trip_b_unit                     );
        mVehcledataTag_StringToInt = Collections.unmodifiableMap(map);
    }
}
