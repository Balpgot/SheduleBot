import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class Main extends TelegramLongPollingBot{
    private static String BOT_NAME = "UTUiE_bot";
    private static String BOT_TOKEN = "" /* your bot's token here */;

    //private static String PROXY_HOST = "207.180.242.44" /* proxy host */;
    //private static Integer PROXY_PORT = 8080 /* proxy port */;
    private static List<List<String>> firstWeek;
    private static List<List<String>> secondWeek;
    private static final int WEEK = 7;
    private static final int LESSONS = 6;
    private static ZoneId moscow = ZoneId.of("Europe/Moscow");
    private static ZonedDateTime dateCurrent;
    private static ZonedDateTime dateStart;
    /*TO DO
     * Add dateEnd and refactor all methods
     * */

    public static void main(String[] args) {
        try {
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            //botOptions.setProxyHost(PROXY_HOST);
            //botOptions.setProxyPort(PROXY_PORT);
            //botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);

            Main bot = new Main();
            botsApi.registerBot(bot);
            bot.setFirstWeek();
            bot.setSecondWeek();
            bot.setDateStart();



        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /*protected Main(DefaultBotOptions botOptions) {
        super(botOptions);
    }*/

    public void onUpdateReceived(Update update) {
        try {
            setDateCurrent();
            if(update.getMessage().hasEntities()){
                List<MessageEntity> list = new ArrayList<>();
                list.addAll(update.getMessage().getEntities());
                for(MessageEntity ME : list){
                    //System.out.println(ME.getText());
                    if(ME.getText().equalsIgnoreCase("/start")){
                        this.sendCustomKeyboard(update.getMessage().getChatId());
                        break;
                    }
                    else{
                        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setText("Отправьте боту команду /start");
                        execute(message);
                    }
                }

            }
            else if (update.hasMessage() && update.getMessage().hasText()) {
                SendMessage msg;
                String message = update.getMessage().getText();
                boolean dateMessage = Pattern.matches("\\d{2}[.]\\d{2}",message);
                System.out.println(update.getMessage().getChatId());
                switch (message) {
                    case ("Сегодня"): {
                        getSheduleOnDay(this.getWeek(dateCurrent),this.getDay(dateCurrent),update.getMessage().getChatId());
                        break;
                    }
                    case ("Завтра"): {
                        getSheduleTommorow(this.getWeek(dateCurrent),this.getDay(dateCurrent),update.getMessage().getChatId());
                        break;
                    }
                    case ("Эта неделя"): {
                        getSheduleOnWeek(this.getWeek(dateCurrent),update.getMessage().getChatId());
                        break;
                    }
                    case ("Следующая неделя"): {
                        getSheduleOnWeek(this.getWeek(dateCurrent)+1,update.getMessage().getChatId());
                        break;
                    }
                    case ("Поиск по числу"): {
                        msg = new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText("Введите число \nПример: 01.06");
                        execute(msg);
                        break;
                    }
                    default:{
                        if(dateMessage){
                            getSheduleOnDate(message, update.getMessage().getChatId());
                        }
                    }
                }
            }
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void setFirstWeek(){
        firstWeek = new ArrayList<>();
        List<String> monday = new ArrayList<>();
        monday.add("08:25-09:55/К-сп.зал/Физическая культура и спорт/Осипова Л.Ф.");
        monday.add("10:00-11:30/Л-Акт.зал/Гражданское право/Алексеева Ю.С.");
        monday.add("11:40-13:10/Л-Акт.зал/Гражданское право/Алексеева Ю.С.");
        monday.add("13:50-15:20/Л-317/Безопасность жизнедеятельности/Сауц А.В.");
        monday.add("");
        monday.add("");
        firstWeek.add(monday);
        List<String> tuesday = new ArrayList<>();
        tuesday.add("");
        tuesday.add("10:00-11:30/Ш-19/Гражданский процесс/Смирнова Е.М.");
        tuesday.add("11:40-13:10/Ш-19/Гражданский процесс/Смирнова Е.М.");
        tuesday.add("");
        tuesday.add("");
        tuesday.add("");
        firstWeek.add(tuesday);
        List<String> wednesday = new ArrayList<>();
        wednesday.add("");
        wednesday.add("10:00-11:30/Ш-18/Гражданское право/Костиков В.В.");
        wednesday.add("11:40-13:10/Ш-18/Гражданское право/Костиков В.В.");
        wednesday.add("13:50-15:20/Ш-3/Конституционное право/Святогорова А.Э.");
        wednesday.add("15:30-17:00/Ш-3/Конституционное право/Святогорова А.Э.");
        wednesday.add("");
        firstWeek.add(wednesday);
        List<String> thursday = new ArrayList<>();
        thursday.add("Занятий нет");
        thursday.add("");
        thursday.add("");
        thursday.add("");
        thursday.add("");
        thursday.add("");
        firstWeek.add(thursday);
        List<String> friday = new ArrayList<>();
        friday.add("");
        friday.add("");
        friday.add("");
        friday.add("");
        friday.add("15:30-17:00/Л-211/Уголовное право/Белозёров Р.В.");
        friday.add("17:10-18:40/Л-207/Арбитражный процесс/Тихонравов Л.В.");
        firstWeek.add(friday);
        List<String> saturday = new ArrayList<>();
        saturday.add("Занятий нет");
        saturday.add("");
        saturday.add("");
        saturday.add("");
        saturday.add("");
        saturday.add("");
        firstWeek.add(saturday);
        List<String> sunday = new ArrayList<>();
        sunday.add("Занятий нет");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        firstWeek.add(sunday);
    }

    public void setSecondWeek(){
        secondWeek = new ArrayList<>();
        List<String> monday = new ArrayList<>();
        monday.add("");
        monday.add("10:00-11:30/Ш-15/Институт государственно-правовой охраны конституционных прав и свобод граждан/Соловьева Н.В.");
        monday.add("11:40-13:10/Ш-15/Институт государственно-правовой охраны конституционных прав и свобод граждан/Соловьева Н.В.");
        monday.add("13:50-15:20/Ш-сп.зал/Физическая культура и спорт(электив)/Осипова Л.Ф.");
        monday.add("15:30-17:00/Ш-сп.зал/Физическая культура и спорт(электив)/Осипова Л.Ф.");
        monday.add("");
        secondWeek.add(monday);
        List<String> tuesday = new ArrayList<>();
        tuesday.add("");
        tuesday.add("");
        tuesday.add("");
        tuesday.add("13:50-15:20/Л-415/Безопасность жизнедеятельности/Сауц А.В.");
        tuesday.add("15:30-17:00/Л-415/Уголовное право/Белозёров Р.В.");
        tuesday.add("17:10-18:40/Л-319/Уголовное право/Белозёров Р.В.");
        secondWeek.add(tuesday);
        List<String> wednesday = new ArrayList<>();
        wednesday.add("");
        wednesday.add("");
        wednesday.add("11:40-13:10/Л-415/Конституционное право/Святогорова А.Э.");
        wednesday.add("13:50-15:20/Л-415/Конституционное право/Святогорова А.Э.");
        wednesday.add("15:30-17:00/Л-415/Муниципальное право/Соловьева Н.В.");
        wednesday.add("17:10-18:40/Л-321/Муниципальное право/Соловьева Н.В.");
        secondWeek.add(wednesday);
        List<String> thursday = new ArrayList<>();
        thursday.add("08:25-09:55/Л-315/Муниципальная служба в Российской Федерации/Воскресенская Е.В.");
        thursday.add("10:00-11:30/Л-315/Муниципальная служба в Российской Федерации/Воскресенская Е.В.");
        thursday.add("");
        thursday.add("");
        thursday.add("");
        thursday.add("");
        secondWeek.add(thursday);
        List<String> friday = new ArrayList<>();
        friday.add("Занятий нет");
        friday.add("");
        friday.add("");
        friday.add("");
        friday.add("");
        friday.add("");
        secondWeek.add(friday);
        List<String> saturday = new ArrayList<>();
        saturday.add("08:25-09:55/Л-415/Гражданский процесс/Саченко А.Л.");
        saturday.add("10:00-11:30/Л-415/Арбитражный процесс/Саченко А.Л.");
        saturday.add("");
        saturday.add("");
        saturday.add("");
        saturday.add("");
        secondWeek.add(saturday);
        List<String> sunday = new ArrayList<>();
        sunday.add("Занятий нет");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        sunday.add("");
        secondWeek.add(sunday);
    }

    public void setDateStart(){
        dateStart = ZonedDateTime.of(2019,2,4,0,0,0,0,moscow);
    }

    public void setDateCurrent(){
        dateCurrent = ZonedDateTime.now(moscow);
    }

    public void getSheduleOnDate(String date, long chatId){
        String [] dayAndMonth = date.split("\\.");
        String day = "", month = "";
        if(dayAndMonth[0].charAt(0) == '0'){
            day+=dayAndMonth[0].charAt(1);
        }
        else{
            day=dayAndMonth[0];
        }
        if(dayAndMonth[1].charAt(0) == '0'){
            month+=dayAndMonth[1].charAt(1);
        }
        else{
            month=dayAndMonth[1];
        }
        ZonedDateTime givenDate = ZonedDateTime.of(2019,Integer.parseInt(month),Integer.parseInt(day),0,0,0,0,moscow);
        getSheduleOnDay(getWeek(givenDate),getDay(givenDate),chatId);
    }

    public void getSheduleOnWeek(int week, long chatId){
        try{
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<WEEK; i++){
                switch (i){
                    case(0): sb.append("ПОНЕДЕЛЬНИК\n"); break;
                    case(1): sb.append("ВТОРНИК\n"); break;
                    case(2): sb.append("СРЕДА\n"); break;
                    case(3): sb.append("ЧЕТВЕРГ\n"); break;
                    case(4): sb.append("ПЯТНИЦА\n"); break;
                    case(5): sb.append("СУББОТА\n"); break;
                    case(6): sb.append("ВОСКРЕСЕНЬЕ\n"); break;
                }
                if(week%2 == 0){
                    sb.append(sheduleToString(secondWeek.get(i)));
                }
                else
                    sb.append(sheduleToString(firstWeek.get(i)));
            }
            msg.setText(sb.toString());
            execute(msg);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

    public void getSheduleOnDay(int week, int day, long chatId){
        try{
            if(week%2 == 0){
                SendMessage msg = new SendMessage().setChatId(chatId).setText(sheduleToString(secondWeek.get(day)));
                execute(msg);
            }
            else{
                SendMessage msg = new SendMessage().setChatId(chatId).setText(sheduleToString(firstWeek.get(day)));
                execute(msg);
            }
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public void getSheduleTommorow(int week, int day, long chatId){
        if(day==6){
            week++;
            day=0;
        }
        else{
            day++;
        }
        try{
            if(week%2 == 0){
                SendMessage msg = new SendMessage().setChatId(chatId).setText(sheduleToString(secondWeek.get(day)));
                execute(msg);
            }
            else{
                SendMessage msg = new SendMessage().setChatId(chatId).setText(sheduleToString(firstWeek.get(day)));
                execute(msg);
            }
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public int getWeek(ZonedDateTime date){
        int week = 0;
        ZonedDateTime tempDate = dateStart;
        while(tempDate.isBefore(date)){
            week++;
            tempDate = tempDate.plusWeeks(1);
        }
        System.out.println(tempDate);
        if((tempDate.getDayOfMonth()+6>date.getDayOfMonth())&&tempDate.getDayOfMonth()!=date.getDayOfMonth()){
            week--;
        }
        System.out.println(week);
        return week;

    }

    public int getDay(ZonedDateTime date){
        switch (date.getDayOfWeek()){
            case MONDAY: return 0;
            case TUESDAY: return 1;
            case WEDNESDAY: return 2;
            case THURSDAY: return 3;
            case FRIDAY: return 4;
            case SATURDAY: return 5;
            case SUNDAY: return 6;
            default: return -1;
        }
    }

    public String sheduleToString(List<String> day){
        StringBuilder builder = new StringBuilder();
        String [] separatedLesson;
        for(int i = 0; i<day.size(); i++){
            if(!day.get(i).isEmpty()) {
                separatedLesson = day.get(i).split("/");
                if(separatedLesson.length == 1){
                    builder.append((i + 1) + ". "+separatedLesson[0]+"\n\n");
                }
                else{
                    builder.append((i + 1) + ". "+separatedLesson[0]+'\n');
                    for (int j = 1; j<separatedLesson.length; j++) {
                        builder.append("    " + separatedLesson[j]);
                        builder.append('\n');
                    }
                    builder.append('\n');
                }

            }
        }
        return builder.toString();
    }

    public void sendCustomKeyboard(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("Сегодня");
        row.add("Завтра");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("Эта неделя");
        row.add("Следующая неделя");
        // Add the second row to the keyboard
        keyboard.add(row);
        //Add the third row to the keyboard
        row = new KeyboardRow();
        row.add("Поиск по числу");
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup).setText("Привет! Используй для работы кнопки в меню.");
        try {
            // Send the message
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "UTUiE_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}
