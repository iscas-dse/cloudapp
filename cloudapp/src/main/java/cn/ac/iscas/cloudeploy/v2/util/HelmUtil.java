package cn.ac.iscas.cloudeploy.v2.util;
import java.io.*;
/**
 * @author Simon Lee
 * @description
 * @date 2017-12-17
 */
 class HelmWinUtil {
    public static void main(String args[]) {
        //helmSearch("mysql");
        deployMysql("mysql");
    }
    public static void helmList() {
        System.out.println("------------------helm list--------------------");
        Process process;
        try {
            //执行命令
            process = Runtime.getRuntime().exec("helm ls");
            //取得命令结果的输出流
            InputStream fis = process.getInputStream();
            //用一个读输出流类去读
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            int i=0;
            //逐行读取输出到控制台
            while ((line = br.readLine()) != null) {
                System.out.println(i+++":"+line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void deployMysql(String path){
        path="D:\\helm\\"+path;
        System.out.println("------------------helm search--------------------");
        Process process;
        try {
            //执行命令
            // helm  --host 133.133.134.96:32143 install --name m -f mysql/values.yaml mysql
            System.out.println("helm install "+path+"\\values.yaml "+ path +" --home " +
                    "C:\\Users\\admin\\.helm");
            process = Runtime.getRuntime().exec("helm install -f "+path+"\\values.yaml "+ path +" --home " +
                            "C:\\Users\\admin\\.helm");
            //取得命令结果的输出流
            InputStream fis = process.getInputStream();
            //用一个读输出流类去读
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            int i=0;
            //逐行读取输出到控制台
            while ((line = br.readLine()) != null) {
                System.out.println(i+++":"+line);
            }
            int exitVal = process.waitFor();
            System.out.println("helm 返回"+exitVal);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void helmSearch(String chartName) {
        System.out.println("------------------helm search--------------------");
        Process process;
        try {
            //执行命令
            System.out.println("helm search "+chartName);
            process = Runtime.getRuntime().exec("helm search "+chartName+" --home C:\\Users\\admin\\.helm");
            //取得命令结果的输出流
            InputStream fis = process.getInputStream();
            //用一个读输出流类去读
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            int i=0;
            //逐行读取输出到控制台
            while ((line = br.readLine()) != null) {
                System.out.println(i+++":"+line);
            }
            int exitVal = process.waitFor();
            System.out.println("helm 返回"+exitVal);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void cmd() {
        System.out.println("------------------helm ls()--------------------");
        Runtime runtime = Runtime.getRuntime();
        try {
            //执行一个exe文件
            runtime.exec("notepad");
            runtime.exec("C:\\Program Files\\Microsoft Office\\OFFICE11\\winword.exe c:\\test.doc");
            //执行批处理
            runtime.exec("c:\\x.bat");
            //执行系统命令
            runtime.exec("cmd /c dir ");
            runtime.exec("cmd /c dir c:\\");
//            //-------------- 文件操作 --------------
            runtime.exec("cmd /c copy c:\\x.bat d:\\x.txt");   //copy并改名
            runtime.exec("cmd /c rename d:\\x.txt x.txt.bak"); //重命名
            runtime.exec("cmd /c move d:\\x.txt.bak c:\\");    //移动
            runtime.exec("cmd /c del c:\\x.txt.bak");          //删除
            //-------------- 目录操作 --------------
            runtime.exec("cmd /c md c:\\_test");          //删除
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
class StreamGobbler extends Thread {
    InputStream is;
    String type;
    OutputStream os;

    StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            if (os != null)
                pw = new PrintWriter(os);

            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
                System.out.println(type + ">" + line);
            }

            if (pw != null)
                pw.flush();
            pw.close();
            br.close();
            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally{

        }
    }
}
