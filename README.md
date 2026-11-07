# Java File Catalogue Portfolio

悉尼大学 **SOFT2412「敏捷软件开发实践」** 课程项目
**Virtual Scroll Access System（VSAS）** 的个人贡献展示。
Assignment 2 为小组开发，Assignment 3 为个人评估。我负责目录浏览、
组合筛选、文件预览和下载交互，并参与跨模块集成、需求追踪与发布回归。

| 个人贡献 | 具体工作 |
| --- | --- |
| 目录筛选与预览 | 按上传者、文件 ID、名称和日期组合查询，提供有字节上限的文件预览。 |
| 下载异常处理 | 对已存在的目标文件、缺失的目标目录等情况提示重试，允许取消。验证已有文件的内容和下载计数。 |
| 需求追踪与发布验证 | 将功能和数据字段对应到测试用例。参与 v1.0.1 发布包的离线回归，原项目记录有 28 项适用检查通过。 |

[查看个人贡献与回归用例](docs/contribution-and-validation.md)

## 运行公开示例

公开示例使用虚构文件和标识符，独立演示上述功能。在仓库根目录使用 JDK 17 或更高版本运行：

```text
javac -d out src/Catalogue.java src/DownloadSession.java src/Demo.java tests/RegressionChecks.java
java -cp out Demo
java -cp out RegressionChecks
```

`Demo` 显示筛选结果和十六进制预览。回归程序输出 **8 项检查**，包括组合筛选、
预览长度、目标路径已占用时的重试、取消和成功复制后的计数。

如需亲自体验下载重试，可运行 `java -cp out Demo --download`。先输入
`examples/field-notes.txt` 触发路径保护，再输入仓库根目录下尚不存在的
`downloaded-notes.txt`，或直接按回车取消。

示例代码由 Codex 辅助整理，演示所选功能和测试方法。原课程项目的发布回归记录与公开示例的 8 项检查分别统计。
