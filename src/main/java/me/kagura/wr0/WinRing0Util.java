package me.kagura.wr0;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import java.util.HashMap;
import java.util.Map;


/**
 * Java通过JNA调用WinRing0.dll实现模拟驱动级别按键
 */
public class WinRing0Util {

    static final String WR0_DLL_NAME = "WinRing0" + (Platform.is64Bit() ? "x64" : "");
    static WR0 WR0_INSTANCE = Native.loadLibrary(WR0_DLL_NAME, WR0.class);
    static U32 U32_INSTANCE = Native.loadLibrary("User32", U32.class);

    // 虚拟键值码对应表，可以根据需要增减
    // 完整对应表可参考：https://www.kagura.me/dev/20181228144652.html
    static Map<String, Integer> virtualKeyMap = new HashMap<String, Integer>() {
        {
            put("Tab", 9);// Tab键
            put("Enter", 13);// Enter键
            put("Shift", 16);// Shift键
            put("Ctrl", 17);// Ctrl键
            put("Alt", 18);// Alt键
            put("Pause", 19);// Pause键
            put("CapsLock", 20);// Caps Lock键
            put("Esc", 27);// Esc键
            put("Space", 32);// Space键
            put("0", 48);// 0键
            put("1", 49);// 1键
            put("2", 50);// 2键
            put("3", 51);// 3键
            put("4", 52);// 4键
            put("5", 53);// 5键
            put("6", 54);// 6键
            put("7", 55);// 7键
            put("8", 56);// 8键
            put("9", 57);// 9键
            put("a", 65);// A键
            put("b", 66);// B键
            put("c", 67);// C键
            put("d", 68);// D键
            put("e", 69);// E键
            put("f", 70);// F键
            put("g", 71);// G键
            put("h", 72);// H键
            put("i", 73);// I键
            put("j", 74);// J键
            put("k", 75);// K键
            put("l", 76);// L键
            put("m", 77);// M键
            put("n", 78);// N键
            put("o", 79);// O键
            put("p", 80);// P键
            put("q", 81);// Q键
            put("r", 82);// R键
            put("s", 83);// S键
            put("t", 84);// T键
            put("u", 85);// U键
            put("v", 86);// V键
            put("w", 87);// W键
            put("x", 88);// X键
            put("y", 89);// Y键
            put("z", 90);// Z键
        }
    };

    /**
     * 获取dll的状态
     */
    public static int GetDllStatus() {
        return WR0_INSTANCE.GetDllStatus();
    }

    /**
     * 销毁
     */
    public static void DeinitializeOls() {
        WR0_INSTANCE.DeinitializeOls();
    }

    /**
     * 初始化
     */
    public static void InitializeOls() {
        WR0_INSTANCE.InitializeOls();
    }

    public static void keyPress(String key) throws Exception {
        KeyDown(key);
        KeyUp(key);
    }

    public static void keyPressWidthShift(String key) throws Exception {
        KeyDown("Shift");
        KeyDown(key);
        KeyUp(key);
        KeyUp("Shift");
    }

    private static void KeyDown(String keyChar) throws Exception {
        int btScancode = virtualCodeToScanCode(virtualKeyMap.get(keyChar));
        KBCWait4IBE();
        WR0_INSTANCE.WriteIoPortByte(100, 210);
        KBCWait4IBE();
        WR0_INSTANCE.WriteIoPortByte(96, (byte) btScancode);
    }

    private static void KeyUp(String keyChar) throws Exception {
        int btScancode = virtualCodeToScanCode(virtualKeyMap.get(keyChar));
        KBCWait4IBE();
        WR0_INSTANCE.WriteIoPortByte(100, 210);
        KBCWait4IBE();
        WR0_INSTANCE.WriteIoPortByte(96, (byte) (btScancode | 128));
    }

    /**
     * 根据虚拟键码获取扫描码
     *
     * @param vcode
     * @return
     */
    private static int virtualCodeToScanCode(int vcode) {
        return U32_INSTANCE.MapVirtualKeyA(vcode, 0);
    }

    private static void KBCWait4IBE() throws Exception {
        int val;
        do {
            PointerByReference ppVoid = new PointerByReference(Pointer.createConstant(8));
            val = WR0_INSTANCE.ReadIoPortByteEx(100, ppVoid.getPointer());
            if (0 > val) {
                throw new Exception("Cannot get the Port");
            }
        } while ((2 & val) > 0);

    }

    /**
     * 如果启动报错java.lang.UnsatisfiedLinkError: Unable to load library 'WinRing0x64':
     * 请将lib目录下的四个文件复制到jdk或jre的bin目录下
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //WinRing0初始化
        WinRing0Util.InitializeOls();
        //获取WinRing0的状态码
        WinRing0Util.GetDllStatus();
        int dllStatus = WinRing0Util.GetDllStatus();
        System.err.println("WinRing0 dllStatus:" + dllStatus);

        //模拟按键输入
        String[] keys = new String[]{"a", "b", "c", "d"};
        for (String key : keys) {
            WinRing0Util.keyPress(key);
            Thread.sleep(500);
        }
        for (String key : keys) {
            WinRing0Util.keyPressWidthShift(key);
            Thread.sleep(500);
        }
        //销毁WinRing0
        WinRing0Util.DeinitializeOls();

    }

    private interface WR0 extends Library {
        // 声明将要调用的DLL中的方法
        int InitializeOls();

        int DeinitializeOls();

        int GetDllStatus();

        int WriteIoPortByte(int port, int portVal);

        int ReadIoPortByteEx(int port, Pointer p);

    }

    private interface U32 extends Library {
        // 声明将要调用的DLL中的方法
        int MapVirtualKeyA(int i, int x);

    }

}
