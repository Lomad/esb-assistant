package com.winning.esb.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.winning.esb.model.SimulationFlowModel;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.SimulationTestLogEnum;
import com.winning.esb.model.ext.SimulationFlowSvcExtModel;
import com.winning.esb.utils.FileUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * 主要用于生成测试日志
 *
 * @author xuehao
 * @date 2017/8/16
 */
@Component
public class TestLogPdfHelper {
    private static String DOC_COMPANY_WINNING = "卫宁健康";
    private static String DOC_AUTHOR = "集成平台";
    private static String DOC_TITLE = "集成平台测试日志";
    private static String DOC_SUBJECT = "测试日志"; //主题

    public static String FILE_PATH_DIR = "/download/pdf/";
    public static String FILE_PATH = null;
    private static String File_TTF = null;
    private static String FILE_LOGO = null;

    private static Font contextFont = null;
    private static Font blodContextFont = null;
    private static Font titleFont = null;
    private static Font smallTitleFont = null;
    private static Font bigTitleFont = null;
    //背景色
    private static BaseColor baseColor = new BaseColor(192, 192, 192, 100);
    //提示信息颜色
    private static BaseColor tipColor = new BaseColor(102, 102, 102, 100);

    private static float padding25 = 25f;
    private static float padding35 = 35f;

    public TestLogPdfHelper() {
        FILE_PATH = FileUtils.getRootPath() + FILE_PATH_DIR;
        FileUtils.createPath(FILE_PATH);
        File_TTF = FileUtils.getRootPath() + "/WEB-INF/classes/project-assets/pdf/STSONG.TTF";
        FILE_LOGO = "/project-assets/pdf/winninglogo.png";
    }

    /**
     * 生成日志PDF主函数
     *
     * @param flowModel        流程信息
     * @param flowSvcExtModels 流程明细信息
     * @param orgAppList       机构与系统的对应关系（item1-机构名称，item2-系统名称）
     */
    public String main(SimulationFlowModel flowModel, List<SimulationFlowSvcExtModel> flowSvcExtModels, List<SimpleObject> orgAppList)
            throws FileNotFoundException, DocumentException {
        String fileName;
        if (!ListUtils.isEmpty(flowSvcExtModels)) {
            fileName = UUID.randomUUID().toString() + ".pdf";
            String filePathName = FILE_PATH + fileName;
            Document document = createDocument();
            PdfWriter writer = createWriter(document, filePathName);
            document.open();

            //生成标题及描述
            createTitleDescription(document, flowModel);

            //生成内容
            String serviceName;
            int m = 0, n;
            for (SimulationFlowSvcExtModel item : flowSvcExtModels) {
                m++;
                serviceName = m + ". " + item.getSvc().getName();
                // 一级标题
                addParagraph(document, serviceName, FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
                //创建服务信息的表格
                createSvcTable(document, item);

                List<SimulationTestStepLogModel> logModels = item.getLogList();
                if (!ListUtils.isEmpty(logModels)) {
                    n = 0;
                    for (SimulationTestStepLogModel itemChild : logModels) {
                        // 二级标题
                        n++;
                        String title = "    第" + n + "次测试";
                        addParagraph(document, title, FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, 30f);
                        //生成表格
                        createTestLogTable(document, itemChild);
                    }
                }
            }

            //生成签字
            createCompanySign(document, orgAppList);

            document.close();
            writer.close();

            fileName = FILE_PATH_DIR + fileName;
        } else {
            fileName = null;
        }
        return fileName;
    }

    /**
     * 生成标题及描述
     */
    private void createTitleDescription(Document document, SimulationFlowModel flowModel) throws DocumentException {
        // 生成文档标题
        addParagraph(document, DOC_TITLE, FontStyle.TITLE, Element.ALIGN_CENTER, padding35);
        addParagraph(document, "测试场景：" + flowModel.getName(), FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
        if (!StringUtils.isEmpty(flowModel.getDesp())) {
            addParagraph(document, "场景描述：" + flowModel.getDesp(), FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
        }
        document.newPage();
    }

    /**
     * 生成测试公司以及签字
     */
    private void createCompanySign(Document document, List<SimpleObject> orgAppList) throws DocumentException {
        document.newPage();
        addParagraph(document, "测试结果确认签字", FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, 10f);
        //生成表格
        PdfPTable table = new PdfPTable(3);
        table.setSpacingBefore(10);
        table.setWidthPercentage(95);
        try {
            table.setTotalWidth(new float[]{300, 300, 300});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        String signTip = "[签名格式：姓名 日期]";
        String[] rows = new String[3];
        //标题
        rows[0] = "厂商";
        rows[1] = "业务系统";
        rows[2] = "工程师签名";
        addTableRows(table, rows, 1, 1, null, baseColor);
        //卫宁
        addTableRows(table, DOC_COMPANY_WINNING);
        addTableRows(table, DOC_AUTHOR);
        addTableRows(table, " ");
        //其他公司
        if (!ListUtils.isEmpty(orgAppList)) {
            for (SimpleObject item : orgAppList) {
                addTableRows(table, item.getItem1());
                addTableRows(table, item.getItem2());
                addTableRows(table, " ");
            }
        }
        //另外生成一些空行
        for (int i = 0; i < 5; i++) {
            rows[0] = " ";
            rows[1] = " ";
            rows[2] = " ";
            addTableRows(table, rows);
        }
        document.add(table);
    }

    /**
     * 创建服务信息表
     */
    private void createSvcTable(Document document, SimulationFlowSvcExtModel flowSvcExtModel) throws DocumentException {
        String content = "    服务名称：" + flowSvcExtModel.getSvc().getCode()
                + "，服务代码：" + flowSvcExtModel.getSvc().getName();
        addParagraph(document, content, FontStyle.CONTEXT, Element.ALIGN_LEFT, padding25);
        content = "    提供方：" + flowSvcExtModel.getProvider().getAppName()
                + "，消费方：" + flowSvcExtModel.getConsumer().getAppName();
        addParagraph(document, content, FontStyle.CONTEXT, Element.ALIGN_LEFT, padding25);
    }

    /**
     * 创建测试日志表
     */
    private void createTestLogTable(Document document, SimulationTestStepLogModel stepLogModel) throws DocumentException {
        //生成表格
        PdfPTable table = new PdfPTable(4);
        table.setSpacingBefore(10);
        table.setWidthPercentage(95);
        try {
            table.setTotalWidth(new float[]{300, 500, 300, 500});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        //测试时间
        addTableRows(table, "开始时间", baseColor);
        addTableRows(table, stepLogModel.getBtime().toString());
        addTableRows(table, "结束时间", baseColor);
        addTableRows(table, stepLogModel.getEtime().toString());
        //结果
        addTableRows(table, "结果", baseColor);
        String resultStr;
        Integer result = stepLogModel.getResult();
        if (result != null) {
            if (result.intValue() == SimulationTestLogEnum.ResultEnum.Success.getCode()) {
                resultStr = "成功";
            } else if (result.intValue() == SimulationTestLogEnum.ResultEnum.Failure.getCode()) {
                resultStr = "失败";
            } else {
                resultStr = "";
            }
        } else {
            resultStr = "";
        }
        addTableRows(table, resultStr);
        addTableRows(table, "耗时时长", baseColor);
        addTableRows(table, stepLogModel.getTime_len().toString() + "ms");
        //请求消息
        addTableRows(table, "请求消息", baseColor);
        addTableRows(table, stepLogModel.getOut_msg(), 1, 3);
        //应答消息
        addTableRows(table, "应答消息", baseColor);
        addTableRows(table, stepLogModel.getAck_msg(), 1, 3);

        document.add(table);
    }

    /**
     * 生成文档对象
     */
    private Document createDocument() {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        // 设置作者信息
        document.addAuthor(DOC_AUTHOR);
        // 设置文档创建日期
        document.addCreationDate();
        // 设置标题
        document.addTitle(DOC_TITLE);
        // 设置值主题
        document.addSubject(DOC_SUBJECT);
        return document;
    }

    /**
     * 创建写入流
     */
    private PdfWriter createWriter(Document document, String filePathName) throws FileNotFoundException, DocumentException {
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePathName));
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        PdfReportM1HeaderFooter headerFooter = new PdfReportM1HeaderFooter(File_TTF);
        writer.setBoxSize("art", PageSize.A4);
        writer.setPageEvent(headerFooter);
        return writer;
    }

    private void addParagraph(Document document, String context, FontStyle style, int align, float padding) {
        if (null == contextFont) {
            initPdfFont();
        }

        Font font;
        switch (style) {
            case BLOD_CONTEXT:
                font = blodContextFont;
                break;
            case TITLE:
                font = titleFont;
                break;
            case SMALL_TITLE:
                font = smallTitleFont;
                break;
            case BIG_TITLE:
                font = bigTitleFont;
                break;
            case CONTEXT:
            default:
                font = contextFont;
                break;
        }

        Paragraph paragraph = new Paragraph(context, font);// 抬头
        paragraph.setAlignment(align); // 居中设置
        paragraph.setLeading(padding);// 设置行间距//设置上面空白宽度
        try {
            document.add(paragraph);
        } catch (Exception ex) {
        }
    }

    public void addTableRows(PdfPTable table, String content) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, 1, 1, null, null);
    }

    public void addTableRows(PdfPTable table, String[] rows) {
        addTableRows(table, rows, 1, 1, null, null);
    }

    public void addTableRowsColor(PdfPTable table, String content, BaseColor color) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, 1, 1, color, null);
    }

    public void addTableRows(PdfPTable table, String content, BaseColor backColor) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, 1, 1, null, backColor);
    }

    public void addTableRows(PdfPTable table, String content, int rowspan, int colspan) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, rowspan, colspan, null, null);
    }

    public void addTableRows(PdfPTable table, String content, int rowspan, int colspan, BaseColor color, BaseColor backColor) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, rowspan, colspan, color, backColor);
    }

    public void addTableRows(PdfPTable table, String[] rows, int rowspan, int colspan, BaseColor color, BaseColor backColor) {
        PdfPCell cell;
        Font font = new Font(contextFont);
        if (null != color) {
            font.setColor(color);
        }
        for (int i = 0, count = rows.length; i < count; i++) {
            String row = rows[i];
            cell = new PdfPCell(new Phrase(row, font));
            cell.setPadding(5f);
            cell.setPaddingBottom(8f);
            cell.setRowspan(rowspan);
            cell.setColspan(colspan);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (null != backColor) {
                cell.setBackgroundColor(backColor);
            }
            table.addCell(cell);
        }
    }

    private void initPdfFont() {
        BaseFont font = null;
        try {
            font = BaseFont.createFont(File_TTF, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != font) {
            contextFont = new Font(font, 12, Font.NORMAL); // 正文字体
            blodContextFont = new Font(font, 14, Font.BOLD); // 正文字体
            titleFont = new Font(font, 18, Font.BOLD); // 正文字体
            smallTitleFont = new Font(font, 14, Font.BOLD); // 正文字体
            bigTitleFont = new Font(font, 22, Font.BOLD); // 正文字体
        }
    }

    enum FontStyle {
        CONTEXT, BLOD_CONTEXT, TITLE, SMALL_TITLE, BIG_TITLE
    }

    class PdfReportM1HeaderFooter extends PdfPageEventHelper {
        /**
         * 页眉
         */
        public String header = "";

        /**
         * 文档字体大小，页脚页眉最好和文本大小一致
         */
        public int presentFontSize = 12;

        /**
         * 文档页面大小，最好前面传入，否则默认为A4纸张
         */
        public Rectangle pageSize = PageSize.A4;

        // 模板
        public PdfTemplate total;

        // 基础字体对象
        public BaseFont baseFont = null;

        // 利用基础字体生成的字体对象，一般用于生成中文文字
        public Font fontDetail = null;

        /**
         * Creates a new instance of PdfReportM1HeaderFooter 无参构造方法.
         */
        public PdfReportM1HeaderFooter(String fileTTF) {
            if (baseFont == null) {
                try {
                    baseFont = BaseFont.createFont(fileTTF, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fontDetail == null) {
                fontDetail = new Font(baseFont, presentFontSize, Font.NORMAL);// 数据体字体
            }
        }

        /**
         * Creates a new instance of PdfReportM1HeaderFooter 构造方法.
         *
         * @param yeMei           页眉字符串
         * @param presentFontSize 数据体字体大小
         * @param pageSize        页面文档大小，A4，A5，A6横转翻转等Rectangle对象
         */
        public PdfReportM1HeaderFooter(String fileTTF, String yeMei, int presentFontSize, Rectangle pageSize) {
            this.header = yeMei;
            this.presentFontSize = presentFontSize;
            this.pageSize = pageSize;
            if (baseFont == null) {
                try {
                    baseFont = BaseFont.createFont(fileTTF, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                } catch (Exception e) {
                }
            }
            if (fontDetail == null) {
                fontDetail = new Font(baseFont, presentFontSize, Font.NORMAL);// 数据体字体
            }
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public void setPresentFontSize(int presentFontSize) {
            this.presentFontSize = presentFontSize;
        }

        /**
         * TODO 文档打开时创建模板
         *
         * @see PdfPageEventHelper#onOpenDocument(PdfWriter,
         * Document)
         */
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 50);// 共 页
            // 的矩形的长宽高
        }

        /**
         * TODO 关闭每页的时候，写入页眉，写入'第几页共'这几个字。
         *
         * @see PdfPageEventHelper#onEndPage(PdfWriter,
         * Document)
         */
        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            Phrase watermark = new Phrase("卫          宁         健         康",
                    new Font(baseFont, 50, Font.NORMAL, new BaseColor(192, 192, 192, 100)));
            PdfContentByte under = writer.getDirectContentUnder();
            ColumnText.showTextAligned(under, Element.ALIGN_CENTER, watermark, 298, 421, 45);

            PdfContentByte headAndFootPdfContent = writer.getDirectContent();
            // headAndFootPdfContent.saveState();
            // headAndFootPdfContent.beginText();
            // //设置中文
            headAndFootPdfContent.setFontAndSize(baseFont, 12);

            // 文档页头信息设置
            // float x = document.top(-10);
            float x1 = document.top(-32);
            // 页头信息中间
            headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, DOC_TITLE, document.right() - 120, x1,
                    0);

            PdfPTable table = new PdfPTable(2);
            try {
                table.setTotalWidth(new float[]{20, 480});
            } catch (DocumentException e1) {
            }
            table.setTotalWidth(527);
            table.setLockedWidth(true);
            table.getDefaultCell().setFixedHeight(20);
            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try {
                InputStream in = TestLogPdfHelper.class.getResourceAsStream(FILE_LOGO);
                BufferedImage bufferImage = ImageIO.read(in);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferImage, "png", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                Image image = Image.getInstance(imageInByte);
                table.addCell(image);
            } catch (BadElementException | IOException e) {
                e.printStackTrace();
            }
            table.addCell("");
            table.writeSelectedRows(0, -1, 34, 837, writer.getDirectContent());

            // 页头信息左面
            headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, DOC_AUTHOR, document.left() + 7, x1, 0);
            // //页头信息中间
            // headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_CENTER,
            // "主数据管理系统接口",
            // (document.right() + document.left()) / 2, x1, 0);
            // //页头信息右面
            // headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_RIGHT,
            // " 单位：册",
            // document.right() - 100, x1, 0);

            // 1.写入页眉
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(header, fontDetail),
                    document.left(), document.top() + 20, 0);

            // 2.写入前半部分的 第 X页/共
            int pageS = writer.getPageNumber();
//            String foot1 = "第 " + pageS + " 页 /共";
            String foot1 = "第 " + pageS + " 页";
            Phrase footer = new Phrase(foot1, fontDetail);

            // 3.计算前半部分的foot1的长度，后面好定位最后一部分的'Y页'这俩字的x轴坐标，字体长度也要计算进去 = len
            float len = baseFont.getWidthPoint(foot1, presentFontSize);

            // 4.拿到当前的PdfContentByte
            PdfContentByte cb = writer.getDirectContent();

            // 5.写入页脚1，x轴就是(右margin+左margin + right() -left()- len)/2.0F
            // 再给偏移20F适合人类视觉感受，否则肉眼看上去就太偏左了
            // ,y轴就是底边界-20,否则就贴边重叠到数据体里了就不是页脚了；注意Y轴是从下往上累加的，最上方的Top值是大于Bottom好几百开外的。
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.rightMargin() + document.right() + document.leftMargin() - document.left() - len) / 2.0F
                            + 20F,
                    document.bottom() - 20, 0);

            // 6.写入页脚2的模板（就是页脚的Y页这俩字）添加到文档中，计算模板的和Y轴,X=(右边界-左边界 -
            // 前半部分的len值)/2.0F + len ， y
            // 轴和之前的保持一致，底边界-20
            cb.addTemplate(total,
                    (document.rightMargin() + document.right() + document.leftMargin() - document.left()) / 2.0F + 20F,
                    document.bottom() - 20); // 调节模版显示的位置

        }

        /**
         * TODO 关闭文档时，替换模板，完成整个页眉页脚组件
         *
         * @see PdfPageEventHelper#onCloseDocument(PdfWriter,
         * Document)
         */
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            // 7.最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
            total.beginText();
            total.setFontAndSize(baseFont, presentFontSize);// 生成的模版的字体、颜色
//            String foot2 = " " + (writer.getPageNumber()) + " 页";
            String foot2 = "";
            total.showText(foot2);// 模版显示的内容
            total.endText();
            total.closePath();
        }
    }

}