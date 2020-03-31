package ml.metrics;

import java.util.*;

public class AUC {
    public static double auc(List<Double> pred, List<Integer> label) {
        int len = pred.size();

        // 正负样本个数
        int pos = 0;
        for (int x : label) {
            pos += x;
        }
        int neg = len - pos;

        // 排序
        double[][] pred_label_rank = new double[len][3];
        for (int i = 0; i < len; ++i) {
            pred_label_rank[i] = new double[]{pred.get(i), label.get(i), -1};
        }
        Arrays.sort(pred_label_rank, (a, b) -> {
            if (a[0] > b[0])
                return +1;
            else if (a[0] < b[0])
                return -1;
            else {
                return Double.compare(a[1], b[1]);
            }
        });

        // 对每个数据对赋予rank
        for (int i = 0, rank = 0; i < len; i++) {
            rank += 1;
            pred_label_rank[i][2] = rank;
        }


        double posRankTotal = 0;

        for (int i = len - 1; i >= 0; ) {

            int j = i - 1;
            int same_count = pred_label_rank[i][1] == 1.0 ? 1 : 0;
            double cur_rank_total = pred_label_rank[i][1] == 1.0 ? pred_label_rank[i][2] : 0.0;
            for (; j >= 0; j--) {
                if (pred_label_rank[j][0] < pred_label_rank[i][0]) {
                    break;
                } else {
                    cur_rank_total += pred_label_rank[j][2];
                    if (pred_label_rank[j][1] == 1.0)
                        same_count += 1;
                }
            }

            // 对有无重复值分别处理
            if (j == i - 1) {
                posRankTotal += cur_rank_total;
            } else {
                posRankTotal += same_count * cur_rank_total / (i - j);
            }

            i = j;
        }

        return (posRankTotal - (pos) * (pos + 1.0) / 2) / (pos * neg);
    }

    public static void main(String[] args) {

        List<Double> pred = Arrays.asList(0.8, 0.3, 0.9, 0.6, 0.1, 0.3, 0.7, 0.3);
        List<Integer> label = Arrays.asList(1, 1, 1, 1, 0, 0, 0, 0);
        System.out.println(auc(pred, label));
    }
}
