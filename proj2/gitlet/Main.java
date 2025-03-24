package gitlet;

import java.io.File;

import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
            //  throw Utils.error("Please enter a command.");
        }
        String firstArg = args[0];

        checkInit(args);

        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                // validateNumArgs(args,1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.add(args);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                //validateNumArgs(args,2);
                Repository.commit(args);
                break;
            case "checkout":
                if (args[1].equals("--")) {
                    validateNumArgs(args, 3);
                } else {
                    if (args.length >= 4 && args[2].equals("--")) {
                        validateNumArgs(args, 4);
                    } else {
                        validateNumArgs(args, 2);
                    }
                }
                Repository.checkout(args);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            default:
                Utils.message("No command with that name exists.");
                System.exit(0);
        }
    }

    //    public static void dumpobj(String[] args){
    //        Repository.dumpobj(args[1]);
    //    }
    /** 检查命令参数数量 */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

    /** 检查是否在初始化的Gitlet工作目录 ,指令执行前的基础判断*/
    public static void checkInit(String[] args) {
        String firstArg = args[0];
        File cwd = new File(System.getProperty("user.dir"));
        File gitletDir = join(cwd, ".gitlet");
        if (!firstArg.equals("init")  && (!gitletDir.exists() || !gitletDir.isDirectory())) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        if (firstArg.equals("init") && gitletDir.exists() && gitletDir.isDirectory()) {
            validateNumArgs(args, 1);
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }
}
