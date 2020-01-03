package com.example.wifiip;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private EditText mIpEt;
    private EditText mGateWayEt;
    private EditText mDns1Et;
    private EditText mDns2Et;
    private Button mConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIpEt = findViewById(R.id.ip_et);
        mGateWayEt = findViewById(R.id.gateway_et);
        mDns1Et = findViewById(R.id.dns1_et);
        mDns2Et = findViewById(R.id.dns2_et);
        mConfirmBtn = findViewById(R.id.confirm_btn);

        requestPermission();

//        StaticIpUtil staticIpUtil = new StaticIpUtil(this);
//        staticIpUtil.getNetworkInformation();

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mIpEt.getText()) ||
                        TextUtils.isEmpty(mGateWayEt.getText()) ||
                        TextUtils.isEmpty(mDns1Et.getText()) ||
                        TextUtils.isEmpty(mDns2Et.getText())) {
                    Toast.makeText(MainActivity.this, "输入不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                setIP(MainActivity.this, "STATIC");
            }
        });

        //getLocalIp();
    }

    private void requestPermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            Toast.makeText(this,"has permission",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"No permission",Toast.LENGTH_SHORT).show();
        }
//        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, "com.huawei.permission.sec.MDM_PHONE_MANAGER")){
//            Toast.makeText(this,"No MDM permission",Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, mdm_permissions, 2);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }

//        if (requestCode == 2) {
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this,"MDM permission granted",Toast.LENGTH_SHORT).show();
//                    break;
//                }
//            }
//        }
    }

    /**
     * context 参数，mode参数为静动态模式，分别为STATIC ,DHCP
     **/
    public void setIP(Context context, String mode) {
        InetAddress inetAddress;
        Object ipConfigurationInstance = null;
        try {
            // 获取ETHERNET_SERVICE参数
            String ETHERNET_SERVICE = (String) Context.class.getField(
                    "ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Class<?> ipConfigurationClass = Class.forName("android.net.IpConfiguration");
            // 获取ethernetManager服务对象
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
//            Object getConfiguration = ethernetManagerClass.getDeclaredMethod(
//                    "getConfiguration").invoke(ethernetManager);

            // 获取在EthernetManager中的抽象类mService成员变量
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            // 修改private权限
            mService.setAccessible(true);
            // 获取抽象类的实例化对象
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class
                    .forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method ms : methods) {
                if (ms.getName().equals("setEthernetEnabled")) {
                    ms.invoke(mServiceObject, true);
                }
            }

            Class<?> staticIpConfig = Class.forName("android.net.StaticIpConfiguration");
            Constructor<?> staticIpConfigConstructor = staticIpConfig.getDeclaredConstructor(staticIpConfig);
            Object staticIpConfigInstance = staticIpConfig.newInstance();
            // 获取LinkAddress里面只有一个String类型的构造方法
            Class<?> linkAddressClass = Class.forName("android.net.LinkAddress");
            Constructor<?> linkAddressConstructor = linkAddressClass.getDeclaredConstructor(String.class);

            // 实例化带String类型的构造方法
            // 192.168.88.22/24--子网掩码长度,24相当于255.255.255.0, 此处必须按照这个样式来设置才行
//            Object linkAddress = (Object) linkAddressConstructor .newInstance("192.168.88.22/24");
            Object linkAddress = (Object) linkAddressConstructor .newInstance(mIpEt.getText() + "/24");

//            Class<?> inetAddressClass = Class.forName("java.net.InetAddress");
//            // 默认网关参数
//            byte[] bytes = new byte[]{(byte) 192, (byte) 168, 88, (byte)254};
//            Constructor<?>[] inetAddressConstructors = inetAddressClass.getDeclaredConstructors();
//            for (Constructor inetc : inetAddressConstructors) {
//                System.out.println("zxl--->inetc--->"+inetc.getParameterTypes().length);
//                // 获取有三种参数类型的构造方法
//                if (inetc.getParameterTypes().length == 3) {
//                    // 修改权限
//                    inetc.setAccessible(true);
//                    WifiManager wm = (WifiManager) context.getSystemService("wifi");
//                    int ipAddressInt = wm.getConnectionInfo().getIpAddress();
//                    // hostName主机名
//                    String hostName = String.format(Locale.getDefault(),
//                            "%d.%d.%d.%d", (ipAddressInt & 0xff),
//                            (ipAddressInt >> 8 & 0xff),
//                            (ipAddressInt >> 16 & 0xff),
//                            (ipAddressInt >> 24 & 0xff));
//                    inetAddress = (InetAddress) inetc.newInstance(2, bytes,
//                            hostName);
//                }
//            }

            // 获取staticIpConfig中所有的成员变量
            Field[] declaredFields = staticIpConfigInstance.getClass()
                    .getDeclaredFields();
//            InetAddress address = InetAddress.getByName("192.168.88.254");
            InetAddress address = InetAddress.getByName(mGateWayEt.getText().toString());
            Class<?> threadClazz = Class.forName("android.net.NetworkUtils");
            Method method = threadClazz.getMethod("numericToInetAddress",
                    String.class);
//            Object inetAddressObject1 = method.invoke(null, "8.8.8.8");
            Object inetAddressObject1 = method.invoke(null, mDns1Et.getText().toString());
//            Object inetAddressObject2 = method.invoke(null, "8.8.4.4");
            Object inetAddressObject2 = method.invoke(null, mDns2Et.getText().toString());
            ArrayList<Object> inetAddresses = new ArrayList<Object>();
            inetAddresses.add(inetAddressObject1);
            inetAddresses.add(inetAddressObject2);
            for (Field f : declaredFields) {
                // 设置成员变量的值
                if (f.getName().equals("ipAddress")) {
                    // 设置IP地址和子网掩码
                    f.set(staticIpConfigInstance, linkAddress);
                } else if (f.getName().equals("gateway")) {
                    // 设置默认网关
                    f.set(staticIpConfigInstance, address);
                } else if (f.getName().equals("domains")) {
                    f.set(staticIpConfigInstance, " ");
                } else if (f.getName().equals("dnsServers")) {
                    // 设置DNS，必须要设置才能成功
                    f.setAccessible(true);
                    f.set(staticIpConfigInstance, inetAddresses);
                }
            }
            Object staticInstance = staticIpConfigConstructor
                    .newInstance(staticIpConfigInstance);
            // 存放ipASSignment枚举类参数的集合
            HashMap ipAssignmentMap = new HashMap();
            // 存放proxySettings枚举类参数的集合
            HashMap proxySettingsMap = new HashMap();
            Class<?>[] enumClass = ipConfigurationClass.getDeclaredClasses();
            for (Class enumC : enumClass) {
                // 获取枚举数组
                Object[] enumConstants = enumC.getEnumConstants();
                if (enumC.getSimpleName().equals("ProxySettings")) {
                    for (Object enu : enumConstants) {
                        // 设置代理设置集合 STATIC DHCP UNASSIGNED PAC
                        proxySettingsMap.put(enu.toString(), enu);
                    }
                } else if (enumC.getSimpleName().equals("IpAssignment")) {
                    for (Object enu : enumConstants) {
                        // 设置以太网连接模式设置集合 STATIC DHCP UNASSIGNED
                        ipAssignmentMap.put(enu.toString(), enu);
                    }
                }
            }
            // 获取ipConfiguration类的构造方法
            Constructor<?>[] ipConfigConstructors = ipConfigurationClass
                    .getDeclaredConstructors();
            Class<?> proxyInfo = Class.forName("android.net.ProxyInfo");
            Method methodProxy = proxyInfo.getMethod("buildDirectProxy",
                    String.class, int.class);
            // Object inetAddressObject1 = method.invoke(null, "8.8.8.8");


            for (Constructor constru : ipConfigConstructors) {
                // 获取ipConfiguration类的4个参数的构造方法
                if (constru.getParameterTypes().length == 4) {// 设置以上四种类型
                    // 初始化ipConfiguration对象,设置参数
                    ipConfigurationInstance = constru.newInstance(
                            ipAssignmentMap.get(mode),
                            proxySettingsMap.get("NONE"), staticInstance,
                            methodProxy.invoke(null, null, 0));
                }
            }
            // 获取ipConfiguration类中带有StaticIpConfiguration参数类型的名叫setStaticIpConfiguration的方法
            Method setStaticIpConfiguration = ipConfigurationClass
                    .getDeclaredMethod("setStaticIpConfiguration",
                            staticIpConfig);
            // 修改private方法权限
            setStaticIpConfiguration.setAccessible(true);
            // 在ipConfiguration对象中使用setStaticIpConfiguration方法,并传入参数
            setStaticIpConfiguration.invoke(ipConfigurationInstance,
                    staticInstance);
            Object ethernetManagerInstance = ethernetManagerClass
                    .getDeclaredConstructor(Context.class,
                            iEthernetManagerClass).newInstance(context,
                            mServiceObject);
            Object resultObj = ethernetManagerClass.getDeclaredMethod("setConfiguration", String.class, ipConfigurationClass).invoke(ethernetManagerInstance,"eth0", ipConfigurationInstance);
            System.out.println("zxl--->resultObj--->"+resultObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到有限网关的IP地址
     *
     * @return
     */
    private String getLocalIp() {

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface.getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();
                Log.i("tag", "网络名字" + interfaceName);

                // 如果是有限网卡
                if (interfaceName.equals("eth0")) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface
                            .getInetAddresses();

                    while (enumIpAddr.hasMoreElements()) {
                        // 返回枚举集合中的下一个IP地址信息
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 不是回环地址，并且是ipv4的地址
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            Log.i("tag", inetAddress.getHostAddress() + "   ");

                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";

    }
}
