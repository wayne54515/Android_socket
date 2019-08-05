using System;
using System.Collections;using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Server
{
    /// <summary>
    /// MainWindow.xaml 的互動邏輯
    /// </summary>
    public partial class MainWindow : Window
    {
        public Socket server;
        public String host = "192.168.168.15";
        public int port = 20001;

        public MainWindow()
        {
            InitializeComponent();
        }

        private void Start(object sender, RoutedEventArgs e)
        {
            receive_msg.Text = "Server Start";
            IPAddress ip = IPAddress.Parse(host);
            server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            server.Bind(new IPEndPoint(ip, port));
            server.Listen(10);            
            
            Thread thread = new Thread(Listen);
            thread.Start();
        }

        private void Listen()
        {
            while (true)
            {
                Socket client = server.Accept();

                Thread receive = new Thread(ReceiveMsg);
                receive.Start(client);
            }   
        }

        public void ReceiveMsg(object client)
        {
            Socket connection = (Socket)client;
            IPAddress clientIP = (connection.RemoteEndPoint as IPEndPoint).Address;
            receive_msg.Dispatcher.BeginInvoke(
                new Action(() => { receive_msg.Text += "\n" + clientIP + " connect\n"; }), null);
            while (true)
            {
                try
                {
                    byte[] result = new byte[1024];
                    int receive_num = connection.Receive(result);
                    String receive_str = Encoding.ASCII.GetString(result, 0, receive_num);
                    if (receive_num > 0)
                    {
                        String send_str = "Receive : " + receive_str;

                        connection.Send(Encoding.ASCII.GetBytes(send_str));

                        receive_msg.Dispatcher.BeginInvoke(
                            new Action(() => { receive_msg.Text += send_str; }), null);
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e);
                    connection.Shutdown(SocketShutdown.Both);
                    connection.Close();
                    break;
                }
            }
        }
    }
}
