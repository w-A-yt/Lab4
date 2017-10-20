package com.runnoob.test;

import com.runnoob.test.GraphViz;
import java.io.BufferedReader;  
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;



//邻接矩阵图
/**
 *
 * @author user1
 *
 */
class ListDG {

  final int len = 1000;
  private static int iNF = Integer.MAX_VALUE;

  //链表的顶点
  private class ENode {
    //该边所指向的顶点的位置
    private int ivex;
    //指向下一条弧的指针
    ENode nextEdge;
    //权重
    int weight;
  }

  //表的顶点
  private class VNode {
    //顶点信息
    String data;
    //指向第一条依附该顶点的弧
    ENode firstEdge;
    //该点有几条路径可以走
    int k = 0;
    VNode()
    {
      
    }
  }

  //顶点数组
  public VNode[] mvexs;
  
  
  //创建图
  public ListDG(String[] vexs, String[][] edges) {



    //初始化顶点数和边数
    int vlen = vexs.length;
    int elen = edges.length;

    //初始化顶点
    mvexs = new VNode[vlen];
    for (int i = 0, l = mvexs.length;  i < l; i++) {
      mvexs[i] = new VNode();
      mvexs[i].data = vexs[i];
      mvexs[i].firstEdge = null;
    }

    //初始化边
    for (int i = 0; i < elen; i++) {
      // 读取边的起始顶点和结束顶点
      String c1 = edges[i][0];
      String c2 = edges[i][1];
      // 读取边的起始顶点和结束顶点
      int p1 = getPosition(edges[i][0]);
      int p2 = getPosition(edges[i][1]);

      ENode node2 = mvexs[p1].firstEdge;
      int flag = 0;
      while (node2 != null) {
        if (node2.ivex == p2) {
          flag = 1;
          node2.weight ++;
        }
        node2 = node2.nextEdge;
      }
      if (flag == 0) {
        // 初始化node1
        ENode node1 = new ENode();
        node1.ivex = p2;
        node1.weight = 1;
        node1.nextEdge = null;
        if (mvexs[p1].firstEdge == null) {
          mvexs[p1].firstEdge = node1;
        } else {
          linkLast(mvexs[p1].firstEdge, node1);
        }  
      }
      // 将node1链接到"p1所在链表的末尾"
    }

    //初始化数值k
    for (int i = 0 , l = mvexs.length; i < l; i++) {
      ENode node = mvexs[i].firstEdge;
      while (node != null) {
        mvexs[i].k += 1;
        node = node.nextEdge;
      }
    }
  }

  //获取边<start, end>的权值；若start和end不是连通的，则返回无穷大。
  private int getWeight(int start, int end) {
    if (start == end) {
      return 0;
    }
    ENode node = mvexs[start].firstEdge;
    while (node != null) {
      if (end == node.ivex) {
        return node.weight;
      }
      node = node.nextEdge;
    }

    return iNF;
  }

  //将node节点链接到list的最后
  private void linkLast(ENode list, ENode node) {
    ENode p = list;
    while (p.nextEdge != null) {
      p = p.nextEdge;
    }
    p.nextEdge = node;
  }

  //返回ch的位置
  private int getPosition(String ch) {
    for (int i = 0 , l = mvexs.length; i < l; i++) {
      if (ch.equals(mvexs[i].data)) {
        return i;
      }
    }
    return -1;
  }


  public void getpath(int[][] path,int start,int end) {
    GraphViz gv = new GraphViz();
    int startofp;
    int endofp;
    startofp = start;
    endofp = end;
    if (gv.empty() == 0) {
      gv.addln(gv.start_graph());
      int vlen = mvexs.length;
      for (int i = 0; i < vlen ; i++) {
        ENode node = mvexs[i].firstEdge;
        while (node != null) {
          String edge;
          edge = mvexs[i].data + "->" + mvexs[node.ivex].data + " [label = " + node.weight + "]";
          gv.addln(edge);
          node = node.nextEdge;
        }
      }
      gv.addln(gv.end_graph());
    }

    String color = "[color = red]";
    while (startofp != path[startofp][endofp]) {
      //p2 = p1;
      gv.changeAll(mvexs[startofp].data + "->" + mvexs[path[startofp][endofp]].data,
          mvexs[startofp].data + "->" + mvexs[path[startofp][endofp]].data + color);
      startofp = path[startofp][endofp];
    }
    gv.increaseDpi();   // 106 dpi
    String type = "jpg";
    String repesentationType = "dot";

    File out = new File("E:/xxx." + type);    // Windows
    gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);
  }

  public void calcShortestPath(String word1, String word2) {
    int w1 = iNF;
    int w2 = iNF;
    for (int i = 0; i < mvexs.length; i++) {
      if (mvexs[i].data.equals(word1)) {
        w1 = i; 
      }
      if (mvexs[i].data.equals(word2)) {
        w2 = i;
      }
    }

    int[][] path = new int[mvexs.length][mvexs.length];
    int[][] dist = new int[mvexs.length][mvexs.length];

    // 初始化
    for (int i = 0 , l = mvexs.length; i < l; i++) {
      for (int j = 0 , l_1 = mvexs.length; j < l_1; j++) {
        dist[i][j] = getWeight(i, j);  // "顶点i"到"顶点j"的路径长度为"i到j的权值"。
        path[i][j] = j;                // "顶点i"到"顶点j"的最短路径是经过顶点j。
      }
    }

    // 计算最短路径
    for (int k = 0; k < mvexs.length; k++) {
      for (int i = 0; i < mvexs.length; i++) {
        for (int j = 0; j < mvexs.length; j++) {

          // 如果经过下标为k顶点路径比原两点间路径更短，则更新dist[i][j]和path[i][j]
          int tmp = (dist[i][k] == iNF || dist[k][j] == iNF) ? iNF : (dist[i][k] + dist[k][j]);
          if (dist[i][j] > tmp) {
            // "i到j最短路径"对应的值设，为更小的一个(即经过k)
            dist[i][j] = tmp;
            // "i到j最短路径"对应的路径，经过k
            path[i][j] = path[i][k];
          }
        }
      }
    }

    int result = dist[w1][w2];

    // 打印floyd最短路径的结果
    System.out.printf("floyd: \n");
    for (int i = 0; i < mvexs.length; i++) {
      for (int j = 0; j < mvexs.length; j++) {
        System.out.printf("%10d  ", dist[i][j]);
      }
      System.out.printf("\n");
    }

    //System.out.println("最短路径为：");
    //System.out.print("最短路径长度为：");
    if (result == 0) {
      System.out.println("该点到其他点的最短路径为：");
      for (int i = 1;i < mvexs.length;i++) {
        System.out.print(mvexs[i].data + ":");
        System.out.println(dist[w1][i]);
 
      }
    } else if (result == iNF) {
      System.out.println("不可达。");
    } else {
      System.out.print("最短路径长度为：");
      System.out.println(result);
      getpath(path,w1,w2);
    }

    System.out.println();

  }

  // 打印矩阵队列图
  public void print() {
    System.out.printf("List Graph:\n");
    for (int i = 0; i < mvexs.length; i++) {
      System.out.printf("%d(%s %d): ", i, mvexs[i].data,mvexs[i].k);
      ENode node = mvexs[i].firstEdge;
      while (node != null) {
        node = node.nextEdge;
      }
    }
    System.out.printf("\n\n\n\n");
  }

  //展示有向图的函数
  public void showDirectedGraph() {
    //屏幕输出有向图
    print();
    //图形形式保存至磁盘
    GraphViz gv = new GraphViz();
    if (gv.empty() == 0) {
      gv.addln(gv.start_graph());
      int vlen = mvexs.length;
      for (int i = 0; i < vlen ; i++) {
        ENode node = mvexs[i].firstEdge;
        while (node != null) {
          String edge;
          edge = mvexs[i].data + "->" + mvexs[node.ivex].data + " [label = " + node.weight + "]";
          gv.addln(edge);
          node = node.nextEdge;
        }
      }
      gv.addln(gv.end_graph());
    }

    gv.increaseDpi();   // 106 dpi
    String type = "jpg";
    String repesentationType = "dot";

    File out = new File("E:/out." + type);    // Windows
    gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);

  }

  //查询桥接词
  public String queryBridgeWords(String word1, String word2) {
    //re1是这个函数的返回值
    
    StringBuffer s = new StringBuffer("");
    //flag3记录查找到几个桥接词
    int flag3 = 0;
    for (int i = 0; i < mvexs.length; i++) {
      //找到word1
      if ((mvexs[i].data).equals(word1)) {
        ENode node = mvexs[i].firstEdge;
        while (node != null) {
          //node2.data 就是可能的桥接词
          VNode node2 = mvexs[node.ivex];
          ENode node3 = node2.firstEdge;
          while (node3 != null) {
            if ((mvexs[node3.ivex].data).equals(word2)) {            
              s.append(node2.data);   
              s.append("  ");
              flag3 += 1;
              break; //这里的break跳出一层循环
            }
            node3 = node3.nextEdge;
          }
          node = node.nextEdge;
        }
      }
    }
    return s.toString();
  }
    
  //根据桥接词生成新文本的函数
  //输入的参数是，新文本、邻接表中的存储顶点的字符串数组s4
  public String generateNewText(final String inputText) {
    StringBuffer newText = new StringBuffer("");
    // inputText变成字符串数组sen1
    String[] sen1 = inputText.split("\\s{1,}");

    for (int i = 0; i < sen1.length - 1; i++) {
      String word1 = sen1[i];
      String word2 = sen1[i + 1];
      // 检查word1 word2是否有桥接词
      // 如果有，输出word1和桥接词
      // 如果没有，只输出word1

      // 判断word1 word2是否存在
      int flag1 = 0;
      int flag2 = 0;
      for (int j = 0; j < mvexs.length; j++) {
        if (word1.equals(mvexs[j].data)) {
          flag1 += 1;
          break;
        }
      }
      for (int j = 0; j < mvexs.length; j++) {
        if (word2.equals(mvexs[j].data)) {
          flag2 += 1;
          break;
        }
      }

      // word1或者word2不是有向图中的顶点
      // 即无桥接词
      if (flag1 == 0 || flag2 == 0) {
        newText.append(word1);
        newText.append(" ");
      } else {
        String re1 = queryBridgeWords(word1, word2);
        // 无桥接词
        if (re1.equals("")) {
          newText.append(word1);
          newText.append(" ");
        } else {
          newText.append(word1);
          newText.append(" ");
          newText.append(re1);
          newText.append(" ");
        }
      }
    }
    int k = sen1.length;
    newText.append(sen1[k - 1]);
    return newText.toString();
  }
  /**
   *
   * @return rand.
   */
  public String randomWalk() {
    String str = "";
    // 用来记录遍历过的边
    int[][]visit = new int[len][len];
    // 遍历的第一个点
    int random1 = (int) (Math.random() * (mvexs.length));
    str += mvexs[random1].data;
    str += " ";
    int flag = 1;
    while (flag == 1) {
      // 若该顶点无其他路径
      if (mvexs[random1].k == 0) {
        flag = 0;
      } else {
        // 随机遍历下一个点
        int random2
            = (int) (Math.random() * (mvexs[random1].k));
        ENode node;
        node = mvexs[random1].firstEdge;
        final int exist=1;
        if (visit[random1][node.ivex] ==  exist) {
          str += mvexs[node.ivex].data;
          str += " ";
          flag = 0;
          continue;
        }

        for (int i = 1; i < random2; i++) {
          node = node.nextEdge;
        }
        // 表示该边已经遍历过
        visit[random1][node.ivex] = 1;
        str += mvexs[node.ivex].data;
        str += " ";
        random1 = node.ivex;
      }
    }
    return str;
  }

}

/*HelloWorld类*/
/**
 *
 * @author user1
 *
*/
public final class HelloWorld {
  /**
   * hello.
  */
  private HelloWorld() { }
  /* 主函数 */
  /**
   * da.
   * @param args da
 * @throws IOException 
  */

  public static void main(final String[] args) throws IOException {
    // ~~~~获得项目根目录的绝对路径
    String path = System.getProperty("user.dir");
    System.out.print("文件路径：");
    System.out.println(path);
    // 输入文件名（位置）
    System.out.println("Please input the position of the file:");
    Scanner sca = new Scanner(System.in);
    String filePosition = null;
    filePosition = sca.nextLine();
    File file = new File(filePosition);
    // ~~~~可化类执行
    // ~~~~或用File对象构造一个Scanner对象
    // 字符串构建器
    StringBuilder result = new StringBuilder();
    // ~~~~防止文件不存在抛异常
   
      BufferedReader br = new BufferedReader(
          new FileReader(file));
      String s = null;
      s = br.readLine();
      while (s != null) {
        result.append(s);
        s = br.readLine();
        
      }
      br.close();
   
    String s2 = result.toString();
    // 正则表达式空格代替特殊字符数字
    // ~~~~替换所有匹配给定的正则表达式的子字符串
    s2 = s2.replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×0-9]", " ");
    s2 = s2.toLowerCase();
    // ~~~~s3字符长串化数组
    // 删除一个或多个空格，字符串变为字符串数组
    // s3包含了所有（顶点、边）信息
    String[] s3 = s2.split("\\s{1,}");
    // ~~~~对s3去重
    // s4包含不重复的顶点信息
    List<String> list = new LinkedList<String>();
    for (int i = 0; i < s3.length; i++) {
      if (!list.contains(s3[i])) {
        list.add(s3[i]);
      }
    }
    String[] s4 = list.toArray(new String[list.size()]);
    String[][] s5 = new String[s3.length - 1][2];
    for (int i = 0; i < s3.length - 1; i++) {
      s5[i][0] = s3[i];
      s5[i][1] = s3[i + 1];
    }
    // 生成邻接表有向图
    ListDG pG = new ListDG(s4, s5);
    // 展示邻接表有向图
    pG.showDirectedGraph();
    System.out.println("Please choose the function you need :");
    System.out.println("1. queryBridgeWords ");
    System.out.println("2. generateNewText");
    System.out.println("3. calcShortestPath");
    System.out.println("4. randomWalk");
    Scanner sca1 = new Scanner(System.in);
    String choice = null;    choice = sca1.next();
    if (choice.equals("1")) {
      // 判断是否结束该功能
      int flag = 1;
      while (flag == 1) {
        // 查询桥接词
        // 用户任意输入两个英文单词
        String word1 = null;
        String word2 = null;
        word1 = sca.next();
        word2 = sca.next();
        // 判断word1 word2是否存在
        int flag1 = 0;
        int flag2 = 0;
        for (int i = 0; i < s4.length; i++) {
          if (word1.equals(s4[i])) {
            flag1 += 1;
            break;
          }
        }
        for (int i = 0; i < s4.length; i++) {
          if (word2.equals(s4[i])) {
            flag2 += 1;
            break;
          }
        }
        if (flag1 == 0 || flag2 == 0) {
          System.out.println("No word1 "
              + "or word2 in " + " the"
              + "graph!\r\n");
        } else {
          String re1 = pG.queryBridgeWords(
              word1, word2);
          if (re1.equals("")) {
            System.out.printf("No bridge "
                + "words from \"%s\" to \"%s\"!",
                word1, word2);
          } else {
            System.out.printf("The "
                + "bridge words " + "from \"%s\" "
                + "to \"%s\" are: ", word1, word2);
          }
          System.out.println(re1);
        }
        System.out.println("************************");
        System.out.println("Do you want to continue?");
        Scanner sca3 = new Scanner(System.in);
        String choice2 = null;
        choice2 = sca3.next();
        if (choice2.equals("NO")) {
          flag = 0;
          break;
        }
      }
    } else if (choice.equals("2")) {
      // 根据桥接词生成新文本
      // 用户输入一行新文本
      Scanner sca2 = new Scanner(System.in);
      StringBuffer input1 = new StringBuffer(sca2.nextLine());
      String inputText = input1.toString();
      String new1 = pG.generateNewText(inputText);
      String[] newText = new1.split("\\s{1,}");
      // 调整输出格式
      for (int i = 0; i < newText.length; i++) {
        System.out.print(newText[i]);
        System.out.print(" ");
      }
    } else if (choice.equals("3")) {
      System.out.println("输入顶点：");
      Scanner in1 = new Scanner(System.in);
      String w1 = in1.next();
      String w2 = in1.next();
      pG.calcShortestPath(w1, w2);
    } else if (choice.equals("4")) {
      // 随机游走
      String str = pG.randomWalk();
      System.out.println(str);
      // 文件输出
      try {
        Writer w = new FileWriter("E:/out.txt", true);
        w.write(str);
        w.close();
      } catch (IOException e) {
        System.out.println("文件写入错误：" + e.getMessage());
      }

    } else {
      System.out.println("Nooooo....");
    }
  }
}