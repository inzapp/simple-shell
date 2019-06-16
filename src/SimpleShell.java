import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleShell {

    private static ArrayList<String> historyList;
    private static String dir;

    public static void main(String[] args) throws Exception {
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        historyList = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder("pwd");
        Process p = pb.start();
        dir = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
        while (true) {
            System.out.print("jsh > ");
            commandLine = console.readLine();
            if(commandLine.equals("exit") || commandLine.equals("quit"))
                return;
            if (commandLine.equals(""))
                continue;

            osProcess(commandLine);
        }
    }

    private static void osProcess(String command) {
        historyList.add(command);
        String[] isolatedCommandArr = command.split(" ");
        ArrayList<String> commandList = new ArrayList<>();
        for (String cur : isolatedCommandArr)
            commandList.add(cur);

        if (isolatedCommandArr[0].equals("history")) {
            for (int i = 0; i < historyList.size(); ++i)
                System.out.println(i + " " + historyList.get(i));
            return;
        }

        if(isolatedCommandArr[0].equals("cd")) {
            if(isolatedCommandArr.length == 1) {
                dir = System.getProperty("user.dir");
                return;
            }

            if(isolatedCommandArr[1].equals("..")) {
                String[] isolatedDir = dir.split("/");
                String newDir = "";
                for(int i=0; i<isolatedDir.length - 1; ++i) {
                    newDir += isolatedDir[i];
                    if(i == isolatedDir.length - 1)
                        break;
                    newDir += "/";
                }
                dir = newDir;
                return;
            }

            if(isolatedCommandArr[1].charAt(0) == '/') {
                dir = isolatedCommandArr[1];
                return;
            }

            if(new File(dir + "/" + isolatedCommandArr[1]).isFile()) {
                System.err.println(isolatedCommandArr[1] + " is not directory.");
                return;
            }

            dir = dir + "/" + isolatedCommandArr[1];
            return;
        }

        ProcessBuilder pb = new ProcessBuilder(commandList);
        pb.directory(new File(dir));

        Process process;
        try {
            process = pb.start();
        } catch (Exception e) {
            System.out.println(isolatedCommandArr[0] + " is not process.");
            return;
        }

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            while ((line = br.readLine()) != null)
                System.out.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}