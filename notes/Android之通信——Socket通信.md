## Socket及其通信
- 	套接字,支持TCP/IP协议网络通信的基本单元.是网络通信过程**端点**的抽象.
- 	包含两个IP,两个协议端口,一个协议;
- 	Socket接口的作用:区分不同的应用和连接,实现数据传输的并发服务.
-	**套接字连接过程:**
			
			1. 服务器端不知道客户端的套接字,处于等待请求状态;
			2. 客户端套接字提出请求,以连接服务器端的套接字,描述IP和端口来连接确定;
			3. 服务器端检测到请求之后开启线程发出自身的套接字描述,客户端确认之后建立连接.
			4. 服务器端继续监听等待状态来接收其他请求.

---
### [Socket TCP/UDP通讯示例](https://www.cnblogs.com/zhujiabin/p/5675716.html) ### 
**TCP使用的是流的方式发送，UDP是以包的形式发送。**

---		
## Socket 客户端最简单的TCP通信示例 ##

```java
public class TestSocketCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            connect("192.168.1.24",1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //socket
    private Socket mSocket;
    //线程池
    private ExecutorService mPool;
    //发送信息时的输出流
    private OutputStream mOutputStream;
    //得到消息的输入留
    private InputStream mInputStream;

    public boolean connect(String host, int port) throws IOException {
        if (mSocket == null) {
            mSocket = new Socket(host, port);
            mPool = Executors.newCachedThreadPool();
        }
        return mSocket.isConnected();
    }

    /**
     * 需要在子线程进行
     *
     * @throws IOException
     */
    public String socketMessage() throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mInputStream == null) {
                mInputStream = mSocket.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));

            String s = reader.readLine();
            reader.close();
            return s;
        }else{
            Log.d("TAG","mSocket==null or !mSocket.isConnected()");
        }
        return null;
    }

    /**
     * 需要在子线程进行
     *
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mOutputStream == null) {
                mOutputStream = mSocket.getOutputStream();
            }
            mOutputStream.write(message.getBytes());
            mOutputStream.flush();
        }else{
            Log.d("TAG","mSocket==null or !mSocket.isConnected()");
        }
    }

    public void close() throws IOException {
        if (mSocket != null && mSocket.isConnected()) {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            mSocket.close();
            mSocket = null;
        }
    }

    public void get(){
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = socketMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(){
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessage("这是发送给服务器的消息");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


```