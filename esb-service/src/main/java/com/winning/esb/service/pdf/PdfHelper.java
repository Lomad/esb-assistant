package com.winning.esb.service.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.utils.FileUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xuehao
 * @date 2017/8/16
 */
public class PdfHelper {
    private static String DOC_AUTHOR = "卫宁健康";
    private static String DOC_TITLE = "集成平台接口规范";
    private static String DOC_SUBJECT = "接口规范"; //主题

    public static String FILE_PATH = null;
    private static String FILE_TEMPLATE_PATH_NAME = null;
    private static String File_TTF = null;
    private static String FILE_LOGO = null;

    private static Font contextFont = null;
    private static Font blodContextFont = null;
    private static Font titleFont = null;
    private static Font smallTitleFont = null;
    private static Font bigTitleFont = null;

    private static float padding25 = 25f;
    private static float padding35 = 35f;

    public PdfHelper() {
        FILE_PATH = FileUtils.getRootPath() + "/download/pdf/";
        FILE_TEMPLATE_PATH_NAME = FileUtils.getRootPath() + "/WEB-INF/classes/project-assets/pdf/template.pdf";
        FileUtils.createPath(FILE_PATH);
        File_TTF = FileUtils.getRootPath() + "/WEB-INF/classes/project-assets/pdf/STSONG.TTF";
        FILE_LOGO = "/project-assets/pdf/winninglogo.png";
    }

    /**
     * 用于合并模版与参数pdf
     */
    public boolean mergeToTemplate(List<String> files, String newfile) {
        boolean retValue = false;
        if (!ListUtils.isEmpty(files)) {
            Document document = null;
            try {
                //添加模版文件
                files.add(0, FILE_TEMPLATE_PATH_NAME);

                document = new Document(new PdfReader(files.get(0)).getPageSize(1));
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(newfile));
                document.open();
                for (int i = 0; i < files.size(); i++) {
                    PdfReader reader = new PdfReader(files.get(i));
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++) {
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                    //拼接完成后，删除临时的pdf
                    if (i > 0) {
                        FileUtils.deleteFile(files.get(i));
                    }
                }
                retValue = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                document.close();
            }
        }
        return retValue;
    }

    /**
     * 生成服务说明
     */
    public String createServiceDespcription(Map<String, List<SvcInfoExtModel>> svcInfoExtModelMap)
            throws FileNotFoundException, DocumentException {
        String fileName;
        if (!MapUtils.isEmpty(svcInfoExtModelMap)) {
            fileName = UUID.randomUUID().toString() + ".pdf";
            String filePathName = FILE_PATH + fileName;
            Document document = createDocument();
            PdfWriter writer = createWriter(document, filePathName);
            document.open();
            String titleLevel2;

            for (String groupName : svcInfoExtModelMap.keySet()) {
                if (!ListUtils.isEmpty(svcInfoExtModelMap.get(groupName))) {
                    // 一级标题
                    addParagraph(document, groupName, FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
                    for (SvcInfoExtModel svcInfoExtModel : svcInfoExtModelMap.get(groupName)) {
                        // 二级标题
                        titleLevel2 = svcInfoExtModel.getParagraphIndexLevel1() + "." +
                                svcInfoExtModel.getParagraphIndexLevel2() + "."
                                + svcInfoExtModel.getSvcInfo().getName();
                        addParagraph(document, titleLevel2, FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
                        //生成表格
                        createTable(document, svcInfoExtModel);
                    }
                }
            }

            document.close();
            writer.close();
        } else {
            fileName = null;
        }
        return fileName;
    }

//    /**
//     * 生成服务说明
//     */
//    public String createTestLog(Map<String, List<SimulationTestStepLogModel>> simulationModelMap)
//            throws FileNotFoundException, DocumentException {
//        String fileName;
//        if (!MapUtils.isEmpty(simulationModelMap)) {
//            fileName = UUID.randomUUID().toString() + ".pdf";
//            String filePathName = FILE_PATH + fileName;
//            Document document = createDocument();
//            PdfWriter writer = createWriter(document, filePathName);
//            document.open();
//            for (String serviceName : simulationModelMap.keySet()) {
//                if (!ListUtils.isEmpty(simulationModelMap.get(serviceName))) {
//                    // 一级标题
//                    addParagraph(document, serviceName, FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//                    List<SimulationTestStepLogModel> list = simulationModelMap.get(serviceName);
//                    for (int i = 0; i < list.size(); i++) {
//                        SimulationTestStepLogModel stepLogModel = list.get(i);
//                        // 二级标题
//                        int n = i + 1;
//                        String title = "第" + n + "次测试";
//                        addParagraph(document, title, FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
//                        //生成表格
//                        createTestLogTable(document, stepLogModel);
//                    }
//                }
//            }
//
//            document.close();
//            writer.close();
//        } else {
//            fileName = null;
//        }
//        return fileName;
//    }

//    /**
//     * 应答消息
//     */
//    public String createTestAck(String ackMsg)
//            throws FileNotFoundException, DocumentException {
//        String fileName;
//        if (!StringUtils.isEmpty(ackMsg)) {
//            fileName = UUID.randomUUID().toString() + ".pdf";
//            String filePathName = FILE_PATH + fileName;
//            Document document = createDocument();
//            PdfWriter writer = createWriter(document, filePathName);
//            document.open();
//            addParagraph(document, "应答消息", FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//            createTestAckTable(document, ackMsg);
//            document.close();
//            writer.close();
//        } else {
//            fileName = null;
//        }
//        return fileName;
//    }


//    /**
//     * 应答消息表格
//     */
//    private void createTestAckTable(Document document, String ackMsg) throws DocumentException {
//        //生成表格
//        PdfPTable table = new PdfPTable(2);
//        table.setSpacingBefore(10);
//        table.setWidthPercentage(95);
//        try {
//            table.setTotalWidth(new float[]{300, 1500});
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        BaseColor baseColor = new BaseColor(192, 192, 192, 100);
//
//        //应答消息
//        addTableRowsBackColor(table, "应答消息", baseColor);
//        addTableRows(table, ackMsg);
//        document.add(table);
//    }

//    /**
//     * 测试报告
//     */
//    public String createTestReport(Map<String, List<SimulationTestStepLogModel>> simulationModelMap, SimulationTestLogModel testModel, String appName)
//            throws FileNotFoundException, DocumentException {
//        String fileName;
//        if (!MapUtils.isEmpty(simulationModelMap)) {
//            fileName = UUID.randomUUID().toString() + ".pdf";
//            String filePathName = FILE_PATH + fileName;
//            Document document = createDocument();
//            PdfWriter writer = createWriter(document, filePathName);
//            document.open();
//            addParagraph(document, appName, FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//            addParagraph(document, "概况", FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
//            createTestMainLog(document, testModel);
//            for (String serviceName : simulationModelMap.keySet()) {
//                if (!ListUtils.isEmpty(simulationModelMap.get(serviceName))) {
//                    // 一级标题
//                    addParagraph(document, serviceName, FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//                    List<SimulationTestStepLogModel> list = simulationModelMap.get(serviceName);
//                    for (int i = 0; i < list.size(); i++) {
//                        SimulationTestStepLogModel stepLogModel = list.get(i);
//                        // 二级标题
//                        int n = i + 1;
//                        String title = "第" + n + "次测试";
//                        addParagraph(document, title, FontStyle.SMALL_TITLE, Element.ALIGN_LEFT, padding25);
//                        //生成表格
//                        createTestLogTable(document, stepLogModel);
//                    }
//                }
//            }
//
//            document.close();
//            writer.close();
//        } else {
//            fileName = null;
//        }
//        return fileName;
//    }

//    private void createTestMainLog(Document document, SimulationTestLogModel testModel) throws DocumentException {
//        //生成表格
//        PdfPTable table = new PdfPTable(2);
//        table.setSpacingBefore(10);
//        table.setWidthPercentage(95);
//        try {
//            table.setTotalWidth(new float[]{300, 1500});
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        BaseColor baseColor = new BaseColor(192, 192, 192, 100);
//
//        //测试时间
//        addTableRowsBackColor(table, "测试时间", baseColor);
//        addTableRows(table, testModel.getCtime().toString());
//        //结果
//        addTableRowsBackColor(table, "结果", baseColor);
//        addTableRows(table, testModel.getResult().intValue() == 1 ? "成功" : "失败");
//        //输入消息
//        addTableRowsBackColor(table, "描述", baseColor);
//        addTableRows(table, testModel.getDesp());
//        //输出消息
//        addTableRowsBackColor(table, "耗时", baseColor);
//        addTableRows(table, String.valueOf(testModel.getTime_len()));
//
//        document.add(table);
//    }

//    private void createTestLogTable(Document document, SimulationTestStepLogModel stepLogModel) throws DocumentException {
//        //生成表格
//        PdfPTable table = new PdfPTable(2);
//        table.setSpacingBefore(10);
//        table.setWidthPercentage(95);
//        try {
//            table.setTotalWidth(new float[]{300, 1500});
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        BaseColor baseColor = new BaseColor(192, 192, 192, 100);
//
//        //测试时间
//        addTableRowsBackColor(table, "测试时间", baseColor);
//        addTableRows(table, stepLogModel.getCtime().toString());
//        //结果
//        addTableRowsBackColor(table, "结果", baseColor);
//        addTableRows(table, stepLogModel.getResult().intValue() == 1 ? "成功" : "失败");
//        //输入消息
//        addTableRowsBackColor(table, "输入消息", baseColor);
//        addTableRows(table, stepLogModel.getOut_msg());
//        //输出消息
//        addTableRowsBackColor(table, "输出消息", baseColor);
//        addTableRows(table, stepLogModel.getAck_msg());
//
//        document.add(table);
//    }


    /**
     * 生成文档对象
     */
    private Document createDocument() {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        // 设置作者信息
        document.addAuthor("卫宁健康ESB管理平台");
        // 设置文档创建日期
        document.addCreationDate();
        // 设置标题
        document.addTitle("集成平台接口规范");
        // 设置值主题
        document.addSubject("接口规范");
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

    /**
     * 生成表格
     */
    private void createTable(Document document, SvcInfoExtModel svcInfoExtModel) throws DocumentException {
        SvcInfoModel svcInfo = svcInfoExtModel.getSvcInfo();
        //生成表格
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10);
        table.setWidthPercentage(95);
        table.setTotalWidth(new float[]{300, 1500});
        BaseColor baseColor = new BaseColor(192, 192, 192, 100);
        //服务名称
        addTableRowsBackColor(table, "服务名称", baseColor);
        addTableRows(table, svcInfo.getName());
        //服务代码
        addTableRowsBackColor(table, "服务代码", baseColor);
        addTableRows(table, svcInfo.getCode());
        //服务说明
        addTableRowsBackColor(table, "服务说明", baseColor);
        addTableRows(table, svcInfo.getDesp());
        //请求结构示例
        addTableRowsBackColor(table, "请求结构示例", 1, 2, baseColor);
        addTableRows(table, svcInfoExtModel.getInContent(), 1, 2);
        //应答结构示例
        addTableRowsBackColor(table, "应答结构示例", 1, 2, baseColor);
        addTableRows(table, svcInfoExtModel.getOutContent(), 1, 2);
        document.add(table);
    }

//    //测试函数2
//    public void writeTitle2() throws FileNotFoundException, DocumentException {
//        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
//        // 设置作者信息
//        document.addAuthor(DOC_AUTHOR);
//        // 设置文档创建日期
//        document.addCreationDate();
//        // 设置标题
//        document.addTitle(DOC_TITLE);
//        // 设置值主题
//        document.addSubject(DOC_SUBJECT);
//
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE_PATH + "StylingExample.pdf"));
//        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
//        PdfReportM1HeaderFooter headerFooter = new PdfReportM1HeaderFooter(File_TTF);
//        writer.setBoxSize("art", PageSize.A4);
//        writer.setPageEvent(headerFooter);
//
//        document.open();
//
//        // 一级标题
//        addParagraph(document, "5 业务接口与参数说明", FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//        addParagraph(document, "5.1 查询患者信息(PI102116)", FontStyle.TITLE, Element.ALIGN_LEFT, padding25);
//
//        PdfPTable table = new PdfPTable(5);
//        table.setSpacingBefore(10);
//        table.setTotalWidth(new float[]{200, 250, 550, 200, 200});
//        //服务代码
//        addTableRowsBackColor(table, "服务代码", BaseColor.LIGHT_GRAY);
//        addTableRows(table, "PI102116", 1, 4);
//        //接口说明
//        addTableRows(table, "接口说明");
//        addTableRows(table, "这是测试接口", 1, 4);
//        //字段标题
//        addTableRows(table, "参数类型");
//        addTableRows(table, "参数代码");
//        addTableRows(table, "参数名称");
//        addTableRows(table, "必填");
//        addTableRows(table, "说明");
//        //输入
//        addTableRows(table, "输入", 3, 1);
//        addTableRows(table, "医院代码");
//        addTableRows(table, "yydm");
//        addTableRows(table, "Y");
//        addTableRows(table, "由医院提供");
//        addTableRows(table, "开始日期");
//        addTableRows(table, "ksrq");
//        addTableRows(table, "Y");
//        addTableRows(table, "yyyy");
//        addTableRows(table, "结束日期");
//        addTableRows(table, "jsrq");
//        addTableRows(table, "Y");
//        addTableRows(table, "yyyy");
//
//        document.add(table);
//
//        document.close();
//        writer.close();
//    }

    private void addParagraph(Document document, String context, FontStyle style, int align, float padding) {
        if (null == contextFont) {
            initPdfFont();
        }

        Font font = null;
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

//    public void addTableHeader(PdfPTable table, String[] headers) {
//        PdfPCell cell = null;
//        for (int i = 0, count = headers.length; i < count; i++) {
//            String header = headers[i];
//            cell = new PdfPCell(new Phrase(header, blodContextFont));
//            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(cell);
//        }
//    }
//
//    public void addTableRows(PdfPTable table, String[] rows) {
//        addTableRows(table, rows, 1, 1, null, null);
//    }

    public void addTableRows(PdfPTable table, String content) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, 1, 1, null, null);
    }

    public void addTableRows(PdfPTable table, String content, int rowspan, int colspan) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, rowspan, colspan, null, null);
    }

//    public void addTableRows(PdfPTable table, String content, BaseColor color) {
//        String[] rows = new String[1];
//        rows[0] = content;
//        addTableRows(table, rows, 1, 1, color, null);
//    }

    public void addTableRowsBackColor(PdfPTable table, String content, BaseColor backColor) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, 1, 1, null, backColor);
    }

//    public void addTableRows(PdfPTable table, String content, BaseColor color, BaseColor backColor) {
//        String[] rows = new String[1];
//        rows[0] = content;
//        addTableRows(table, rows, 1, 1, color, backColor);
//    }

//    public void addTableRows(PdfPTable table, String content, int rowspan, int colspan, BaseColor color) {
//        String[] rows = new String[1];
//        rows[0] = content;
//        addTableRows(table, rows, rowspan, colspan, color, null);
//    }

    public void addTableRowsBackColor(PdfPTable table, String content, int rowspan, int colspan, BaseColor backColor) {
        String[] rows = new String[1];
        rows[0] = content;
        addTableRows(table, rows, rowspan, colspan, null, backColor);
    }

    public void addTableRows(PdfPTable table, String[] rows, int rowspan, int colspan,
                             BaseColor color, BaseColor backColor) {
        PdfPCell cell = null;
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
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 50);// 共 页
            // 的矩形的长宽高
        }

        /**
         * TODO 关闭每页的时候，写入页眉，写入'第几页共'这几个字。
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
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
                InputStream in = PdfHelper.class.getResourceAsStream(FILE_LOGO);
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
            headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, "卫宁健康", document.left() + 7, x1, 0);
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
            String foot1 = "第 " + (pageS + 10) + " 页";
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
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
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