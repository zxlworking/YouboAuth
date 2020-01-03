package com.example.wifiip;


import android.content.ContentResolver;
import android.content.Context;
import android.net.LinkAddress;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class StaticIpUtil {
    Context mContext;

    public StaticIpUtil(Context context) {
        mContext = context;
    }

    public void getNetworkInformation() {
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = mWifiManager.getConnectionInfo().getIpAddress();
        Constant.IP = intToIp(ipAddress);
        long getwayIpS = mWifiManager.getDhcpInfo().gateway;
        Constant.gateway = long2ip(getwayIpS);
        // 注意要加\\,要不出不来,yeah
        String[] IPS = Constant.IP.split("\\.");
        Constant.IP = IPS[0] + "." + IPS[1] + "." + IPS[2] + "." + Constant.IPLast;
        Constant.isConnectSocket = IPS[0] + "." + IPS[1] + "." + IPS[2] + "." + Constant.IPLast;
        String zeroIP = "0" + "." + "0" + "." + "0" + "." + Constant.IPLast;
        String equalIP = IPS[0] + "." + IPS[1] + "." + IPS[2] + "." + IPS[3];
        Constant.IP = "10.6.211.27";
        if (!Constant.IP.equals(zeroIP) && !Constant.IP.equals(equalIP)) {
            setIpWithTfiStaticIp(false, Constant.IP, Constant.prefix, Constant.dns1, Constant.gateway);
        }
    }

    /**
     * 网关 。
     *
     * @param ip
     * @return
     */
    public String long2ip(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    public String intToIp(int ipAddress) {
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

    }

    /* *//**
     * 设置静态ip地址的方法
     *//*
    public void changeWifiConfiguration(boolean dhcp, String ip, int prefix, String dns1, String gateway) {

        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            // wifi is disabled
            return;
        }
        // get the current wifi configuration
        WifiConfiguration wifiConfig = null;
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration conf : configuredNetworks) {
                if (conf.networkId == connectionInfo.getNetworkId()) {
                    wifiConfig = conf;
                    break;
                }
            }
        }
        if (wifiConfig == null) {
            // wifi is not connected
            return;
        }
        try {
            Class<?> ipAssignment = wifiConfig.getClass().getMethod("getIpAssignment").invoke(wifiConfig).getClass();
            Object staticConf = wifiConfig.getClass().getMethod("getStaticIpConfiguration").invoke(wifiConfig);
            if (dhcp) {
                wifiConfig.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConfig, Enum.valueOf((Class<Enum>) ipAssignment, "DHCP"));
                if (staticConf != null) {
                    staticConf.getClass().getMethod("clear").invoke(staticConf);
                }
            } else {
                wifiConfig.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConfig, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
                if (staticConf == null) {
                    Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                    staticConf = staticConfigClass.newInstance();
                }
                // STATIC IP AND MASK PREFIX
                Constructor<?> laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
                LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(
                        InetAddress.getByName(ip),
                        prefix);
                staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
                // GATEWAY
                staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName(gateway));
                // DNS
                List<InetAddress> dnsServers = (List<InetAddress>) staticConf.getClass().getField("dnsServers").get(staticConf);
                dnsServers.clear();
                dnsServers.add(InetAddress.getByName(dns1));
                dnsServers.add(InetAddress.getByName(Constant.dns2)); // Google DNS as DNS2 for safety
                // apply the new static configuration
                wifiConfig.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(wifiConfig, staticConf);
            }
            // apply the configuration change
            boolean result = wifiManager.updateNetwork(wifiConfig) != -1; //apply the setting
            if (result) result = wifiManager.saveConfiguration(); //Save it
            if (result) wifiManager.reassociate(); // reconnect with the new static IP
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    /**
     * 设置静态ip地址的方法
     */
    private boolean setIpWithTfiStaticIp(boolean dhcp, String ip, int prefix, String dns1, String gateway) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        boolean flag = false;
        if (!wifiManager.isWifiEnabled()) {
            // wifi is disabled
            return flag;
        }

        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration conf : configuredNetworks) {
                int tempNetWorkId = conf.networkId;
                System.out.println("setIpWithTfiStaticIp start::tempNetWorkId = " + tempNetWorkId + "::ssid = " + conf.SSID + "::ssid = " + conf.SSID.length() + "::" + (TextUtils.equals("\"MIOffice-5G\"", conf.SSID)));
                if (TextUtils.equals("\"OTT\"", conf.SSID)) {
                    wifiManager.removeNetwork(conf.networkId);
                    break;
                }
            }
        }

        for (ScanResult scanResult : wifiManager.getScanResults()) {
            if (TextUtils.equals("OTT", scanResult.SSID)) {
                WifiConfiguration wifiConfiguration = configWifiInfo(mContext, "OTT", "12345678", getType(scanResult));
                int netId = wifiManager.addNetwork(wifiConfiguration);
                boolean success = wifiManager. enableNetwork(netId,true);
                System.out.println("setIpWithTfiStaticIp::success = " + success);
                break;
            }
        }

        WifiConfiguration wifiConfig = null;
        configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration conf : configuredNetworks) {
                int tempNetWorkId = conf.networkId;
                System.out.println("setIpWithTfiStaticIp::tempNetWorkId = " + tempNetWorkId + "::ssid = " + conf.SSID + "::ssid = " + conf.SSID.length() + "::" + (TextUtils.equals("\"MIOffice-5G\"", conf.SSID)));
                if (TextUtils.equals("\"OTT\"", conf.SSID)) {
                    wifiConfig = conf;
                    break;
                }
            }
        }
        if (wifiConfig == null) {
            // wifi is not connected
            return flag;
        }
        if (android.os.Build.VERSION.SDK_INT < 11) { // 如果是android2.x版本的话
            ContentResolver ctRes = mContext.getContentResolver();
            android.provider.Settings.System.putInt(ctRes,
                    android.provider.Settings.System.WIFI_USE_STATIC_IP, 1);
            android.provider.Settings.System.putString(ctRes,
                    android.provider.Settings.System.WIFI_STATIC_IP, "192.168.0.202");
            flag = true;
            return flag;
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) { // 如果是android3.x版本及以上的话
            try {
                setIpAssignment("STATIC", wifiConfig);
                setIpAddress(InetAddress.getByName(ip), prefix, wifiConfig);
                setGateway(InetAddress.getByName(gateway), wifiConfig);
                setDNS(InetAddress.getByName(dns1), wifiConfig);
                int netId = wifiManager.updateNetwork(wifiConfig);
                boolean result = netId != -1; //apply the setting
                if (result) {
                    boolean isDisconnected = wifiManager.disconnect();
                    boolean configSaved = wifiManager.saveConfiguration(); //Save it
                    boolean isEnabled = wifiManager.enableNetwork(wifiConfig.networkId, true);
                    // reconnect with the new static IP
                    boolean isReconnected = wifiManager.reconnect();
                }
             /*   wifiManager.updateNetwork(wifiConfig); // apply the setting
                wifiManager.saveConfiguration(); //Save it*/
                System.out.println("静态ip设置成功！");
                flag = true;
                return flag;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("静态ip设置失败！");
                flag = false;
                return flag;
            }
        } else {//如果是android5.x版本及以上的话
            try {
                Class<?> ipAssignment = wifiConfig.getClass().getMethod("getIpAssignment").invoke(wifiConfig).getClass();
                Object staticConf = wifiConfig.getClass().getMethod("getStaticIpConfiguration").invoke(wifiConfig);
                if (dhcp) {
                    wifiConfig.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConfig, Enum.valueOf((Class<Enum>) ipAssignment, "DHCP"));
                    if (staticConf != null) {
                        staticConf.getClass().getMethod("clear").invoke(staticConf);
                    }
                } else {
                    wifiConfig.getClass().getMethod("setIpAssignment", ipAssignment).invoke(wifiConfig, Enum.valueOf((Class<Enum>) ipAssignment, "STATIC"));
                    if (staticConf == null) {
                        Class<?> staticConfigClass = Class.forName("android.net.StaticIpConfiguration");
                        staticConf = staticConfigClass.newInstance();
                    }
                    // STATIC IP AND MASK PREFIX
                    Constructor<?> laConstructor = LinkAddress.class.getConstructor(InetAddress.class, int.class);
                    LinkAddress linkAddress = (LinkAddress) laConstructor.newInstance(
                            InetAddress.getByName(ip),
                            prefix);
                    staticConf.getClass().getField("ipAddress").set(staticConf, linkAddress);
                    // GATEWAY
                    staticConf.getClass().getField("gateway").set(staticConf, InetAddress.getByName(gateway));
                    // DNS
                    List<InetAddress> dnsServers = (List<InetAddress>) staticConf.getClass().getField("dnsServers").get(staticConf);
                    dnsServers.clear();
                    dnsServers.add(InetAddress.getByName(dns1));
                    //dnsServers.add(InetAddress.getByName(Constant.dns2)); // Google DNS as DNS2 for safety
                    // apply the new static configuration
                    wifiConfig.getClass().getMethod("setStaticIpConfiguration", staticConf.getClass()).invoke(wifiConfig, staticConf);
                }
                // apply the configuration change
                boolean result = wifiManager.updateNetwork(wifiConfig) != -1; //apply the setting
                System.out.println("setIpWithTfiStaticIp::result = " + result);
                if (result) result = wifiManager.saveConfiguration(); //Save it
                if (result) wifiManager.reassociate(); // reconnect with the new static IP
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return flag;
    }


    private static void setIpAssignment(String assign, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }


    private static void setIpAddress(InetAddress addr, int prefixLength,
                                     WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class<?> laClass = Class.forName("android.net.LinkAddress");
        Constructor<?> laConstructor = laClass.getConstructor(new Class[]{

                InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList<Object> mLinkAddresses = (ArrayList<Object>) getDeclaredField(
                linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    private static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    private static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }


    private static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }


    private static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
            throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException,
            InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        if (android.os.Build.VERSION.SDK_INT >= 14) { // android4.x版本
            Class<?> routeInfoClass = Class.forName("android.net.RouteInfo");
            Constructor<?> routeInfoConstructor = routeInfoClass
                    .getConstructor(new Class[]{InetAddress.class});
            Object routeInfo = routeInfoConstructor.newInstance(gateway);
            ArrayList<Object> mRoutes = (ArrayList<Object>) getDeclaredField(

                    linkProperties, "mRoutes");
            mRoutes.clear();
            mRoutes.add(routeInfo);
        } else { // android3.x版本
            ArrayList<InetAddress> mGateways = (ArrayList<InetAddress>) getDeclaredField(

                    linkProperties, "mGateways");
            //    mGateways.clear();
            mGateways.add(gateway);

        }
    }


    private static void setDNS(InetAddress dns, WifiConfiguration wifiConf)

            throws SecurityException, IllegalArgumentException,

            NoSuchFieldException, IllegalAccessException {

        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>)

                getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // 清除原有DNS设置（如果只想增加，不想清除，词句可省略）
        mDnses.add(dns);
        //增加新的DNS
    }

    public static WifiConfiguration configWifiInfo(Context context, String SSID, String password, int type) {
        System.out.println("configWifiInfo::SSID = " + SSID + "--->" + password + "--->" + type);
        WifiConfiguration config = null;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig == null) continue;
                if (existingConfig.SSID.equals("\"" + SSID + "\"")  /*&&  existingConfig.preSharedKey.equals("\""  +  password  +  "\"")*/) {
                    config = existingConfig;
                    break;
                }
            }
        }
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     *获取热点的加密类型
     */
    private int getType(ScanResult scanResult){
        System.out.println("getType::scanResult = " + scanResult.SSID + "--->" + scanResult.capabilities);
        int type;
        if (scanResult.capabilities.contains("WPA"))
            type = 2;
        else if (scanResult.capabilities.contains("WEP"))
            type = 1;
        else
            type = 0;
        return type;
    }
}

