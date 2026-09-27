# Socket Lab Java

Thực hành client-server với Socket trong Java theo [bài GP Coder](https://gpcoder.com/3679-xay-dung-ung-dung-client-server-voi-socket-trong-java/).

Mã nguồn dùng các ví dụ của bài viết, giữ nguyên logic và chú thích. Project được chạy trực tiếp bằng Eclipse IDE for Java Developers trên Windows. Ảnh trong `screenshot/` là ảnh chụp code và console thực tế.

## Mở và chạy bằng Eclipse

1. Chọn **File → Import → General → Existing Projects into Workspace**.
2. Chọn thư mục `socket-lab-java`, nhấn **Finish**. Project dùng JavaSE-21 và UTF-8.
3. Mở lớp cần chạy, chọn **Run As → Java Application** (Alt+Shift+X, J).
4. Chọn console phù hợp bằng **Display Selected Console**. Dùng nút **Terminate** để dừng ứng dụng chạy vòng lặp.

| Bài | Thứ tự chạy | Kết quả thực tế |
| --- | --- | --- |
| TCP tuần tự | `com.gpcoder.tcp.EchoChatSingleServer` → `EchoChatClient` | Client nhận `0 1 2 3 4 5 6 7 8 9`; server ghi nhận kết nối. |
| TCP đa luồng | Dừng server tuần tự; chạy `EchoChatMultiServer`, rồi chạy ba `EchoChatClient` liên tiếp | Ba kết nối được xử lý đồng thời, có ba dòng `Complete processing`. |
| UDP | `com.gpcoder.udp.EchoServer` → `EchoClient` | Nhập lần lượt `Hello` và `How are you?`; cả client và server hiển thị đúng thông điệp. |
| Multicast | `com.gpcoder.multicast.MulticastSender` → nhiều `MulticastReceiver` | Đã thử chạy nhưng gặp `NoRouteToHostException` trên cấu hình mạng hiện tại. Chưa xác nhận truyền/nhận thành công. |

Hai server TCP cùng dùng cổng 7, do đó phải dừng server tuần tự trước khi chạy server đa luồng. UDP cũng dùng cổng 7 nhưng thuộc giao thức khác. Multicast dùng nhóm `224.0.0.1`, cổng `8888`.

## Giải thích code

- `ServerSocket.accept()` chờ kết nối; mỗi kết nối được biểu diễn bằng một `Socket`.
- Client TCP gửi byte biểu diễn các ký tự từ `'0'` đến `'9'`. Server đọc từng byte rồi gửi lại. `read()` trả về `-1` khi phía gửi đóng luồng.
- Server tuần tự xử lý xong một client mới quay lại `accept()`. Server đa luồng giao mỗi socket cho `WorkerThread` thông qua pool 4 luồng. Với hơn 4 công việc đang chạy, công việc tiếp theo phải chờ trong hàng đợi của pool.
- UDP gửi từng `DatagramPacket`; server dùng địa chỉ và cổng trong gói nhận được để trả lời client.
- Multicast sender gửi thông điệp mỗi giây đến địa chỉ nhóm; các receiver tham gia nhóm để nhận. Việc truyền nhận phụ thuộc adapter và cấu hình mạng hỗ trợ multicast.

## Kiểm tra và giới hạn của mẫu

Cả 8 file biên dịch thành công. Eclipse chạy bằng Java 21.0.11 đi kèm IDE; máy cũng có Temurin JDK 21.0.12.1. Có cảnh báo API cũ tại `joinGroup(InetAddress)`, không phải lỗi biên dịch.

Multicast lỗi khi sender gửi và receiver tham gia nhóm. Đã thử receiver với `-Djava.net.preferIPv4Stack=true`, và thử riêng bằng Java 8 đã có trên máy, nhưng chưa khắc phục. Không thay code mẫu để chọn adapter. Cần xử lý cấu hình mạng/quyền quản trị rồi chạy lại để hoàn thành phần multicast.

Mẫu được giữ nguyên nên `WorkerThread` chưa đóng socket sau xử lý; UDP client chưa xử lý đầu vào EOF. Khi thực hành, dừng các chương trình bằng Terminate trong Eclipse.

## Ảnh chụp

- `01-tcp-single-server.png`: server tuần tự chờ kết nối.
- `02-tcp-single-client.png`: client nhận đủ 10 ký tự.
- `03-tcp-single-accepted.png`: server ghi nhận client.
- `04-tcp-multi-client.png`: code client và console server xử lý ba kết nối.
- `05-tcp-multi-server.png`: code server đa luồng và ba kết nối hoàn tất.
- `06-worker-thread.png`: code xử lý từng kết nối.
- `07-udp-client.png`: gửi và nhận hai thông điệp.
- `08-udp-server.png`: server nhận hai thông điệp.
- `09-multicast-sender-error.png`: lỗi multicast sender trên máy thực hành.
- `10-multicast-receiver-error.png`: lỗi multicast receiver trên máy thực hành.
