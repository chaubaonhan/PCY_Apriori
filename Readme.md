# Dự án Khai phá Dữ liệu: Apriori (Java + Hadoop) và PCY (Notebook)

## 🧠 Giới thiệu

Dự án này triển khai hai thuật toán phổ biến trong khai phá tập mục thường xuyên:

- **Apriori**: Cài đặt bằng Java, chạy trên nền tảng **Hadoop MapReduce**.
- **PCY**: Triển khai trong **Jupyter Notebook**, thích hợp để mô phỏng hoặc thử nghiệm với dữ liệu mẫu, có thể tích hợp với **PySpark**.

## 📁 Cấu trúc dự án
```plaintext
D:.
    │   Readme.md
    │   Report.pdf
    │
    ├───Apriori
    │   │   FirstPass.java
    │   │   GroupShopping.java
    │   │   SecondPass.java
    │   │
    │   └───Output
    │           Frequent_1_item.txt
    │           Frequent_pairs.txt
    │           Grouping_customers.txt
    │
    └───PCY
        │   PCY.ipynb
        │
        └───Output
                association_rules.csv
                association_rules.txt
                frequent_1_item.csv
                frequent_pairs.csv
```
## 📌 Mô tả nội dung

### Apriori (Java + Hadoop)

- Cài đặt bằng Java, xử lý tập dữ liệu lớn bằng các job Hadoop MapReduce.
- Áp dụng khai phá luật kết hợp từ dữ liệu giao dịch.
- Thích hợp cho môi trường phân tán (cluster hoặc pseudo-distributed).

### PCY (Jupyter Notebook)

- Triển khai mô phỏng trong notebook, dùng hashing để tối ưu số lượng candidate itemsets.
- Có thể chạy với PySpark nếu xử lý dữ liệu lớn.
- Phù hợp cho việc học thuật, thử nghiệm, hoặc trực quan hóa thuật toán.

## ▶️ Hướng dẫn chạy

### 1. Chạy Apriori trên Hadoop

```markdown
# Biên dịch mã Java
cd apriori/
javac -classpath `hadoop classpath` -d . Apriori.java
jar cf apriori.jar Apriori*.class

# Chạy trên Hadoop
hadoop jar apriori.jar Apriori input_path output_path
```
### 2. Mở PCY Notebook
```markdown
# Mở notebook
cd pcy/
jupyter notebook PCY.ipynb
```

## 📊 Dữ liệu đầu vào

File văn bản hoặc CSV, mỗi dòng là một giao dịch, các item cách nhau bởi dấu phẩy hoặc khoảng trắng.

Có thể sử dụng dữ liệu từ UCI, Kaggle hoặc dữ liệu thực tế của bạn.

## 👨‍💻 Tác giả
Châu Bảo Nhân

Email: chaubaonhan89@gmail.com

## 📄 Giấy phép
Dự án này sử dụng giấy phép MIT License.

---

Giờ thì bạn có thể sao chép và dán vào file `README.md` của mình. Nếu bạn cần thêm bất kỳ sự trợ giúp nào khác, đừng ngần ngại liên hệ nhé! 😊
