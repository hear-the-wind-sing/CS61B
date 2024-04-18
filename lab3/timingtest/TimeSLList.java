package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static  void timeSlisthelper(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts, int N, int M){

        Ns.addLast(N);
        opCounts.addLast(M);
        SLList<Integer> timelist = new SLList<>();

        for(int i = 0; i < N; i++){
            timelist.addLast(i);
        }

        Stopwatch sw = new Stopwatch();

        for (int i = 0; i < M; i++){
            timelist.getLast();
        }

        double timeInSeconds = sw.elapsedTime();
        times.addLast(timeInSeconds);
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int startN = 1000;
        int M = 10000;
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for(int i=0; i < 8; i++){
            timeSlisthelper(Ns, times, opCounts, startN, M);
            startN *= 2;
        }
        printTimingTable(Ns, times , opCounts);
    }

}
