package com.powernode.common.constant;

public class SystemConstant {

    //附近配送员搜索半径
    public static final double  NEARBY_DRIVER_RADIUS = 5;

    //取消订单延迟时间，单位：秒
    public static final int CANCEL_ORDER_DELAY_TIME = 15*60;

    //默认接单距离，单位：公里
    public static final int ACCEPT_DISTANCE = 5;

    //配送员的位置与配送起始点位置的确认距离，单位：米
    public static final int DRIVER_START_LOCATION_DISTION = 1000;

    //配送员的位置与配送终点位置的确认距离，单位：米
    public static final int DRIVER_END_LOCATION_DISTION = 2000;

    //分账延迟时间，单位：秒
    public static final int PROFITSHARING_DELAY_TIME = 2*60;

}
