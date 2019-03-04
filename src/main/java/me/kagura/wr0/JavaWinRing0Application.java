package me.kagura.wr0;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.util.List;

@SpringBootApplication
public class JavaWinRing0Application {

    public static void main(String[] args) {
        System.err.println("如果启动报错java.lang.UnsatisfiedLinkError: Unable to load library 'WinRing0x64':");
        String currentJavaHome = getCurrentJavaHome();
        System.err.println("请将lib目录下的四个文件复制到:" + currentJavaHome.substring(0, currentJavaHome.lastIndexOf("\\")));
        SpringApplication.run(JavaWinRing0Application.class, args);
    }

    /**
     * Windows下获取运行当前程序的java.exe完整路径
     * 例如:C:\Program Files\Java\jdk1.8.0_191\bin\java.exe
     *
     * @return
     */
    public static String getCurrentJavaHome() {
        try {
            //获取程序自己的pid
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            //执行wmic命令获取自己被那个java.exe执行
            String command = String.format("wmic process %s GET ExecutablePath", pid);
            Process process = Runtime.getRuntime().exec(command);
            List<String> lines = IOUtils.readLines(process.getInputStream(), "UTF-8");
            //等待执行完毕
            process.waitFor();
            for (String line : lines) {
                if (line.contains("\\")) return line.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostConstruct
    public void init() {
        //WinRing0初始化
        WinRing0Util.InitializeOls();
        //获取WinRing0的状态码
        WinRing0Util.GetDllStatus();
        int dllStatus = WinRing0Util.GetDllStatus();
        System.err.println("WinRing0 dllStatus:" + dllStatus);
    }

    @PreDestroy
    public void destory() {
        //销毁WinRing0
        WinRing0Util.DeinitializeOls();
    }

}

@RestController
class Action {
    @RequestMapping("/press")
    public Mono<String> press(@RequestParam String keys) {
        int length = keys.length();
        try {
            for (int i = 0; i < length; i++) {
                Thread.sleep(500);
                char key = keys.charAt(i);
                if (Character.isUpperCase(key)) {
                    WinRing0Util.keyPressWidthShift(key + "");
                    continue;
                }
                WinRing0Util.keyPress(key + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.just("ERROR");
        }
        return Mono.just("OK");
    }
}