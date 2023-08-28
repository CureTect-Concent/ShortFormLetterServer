package com.shotFormLetter.sFL;

import com.shotFormLetter.sFL.domain.statistics.domain.entity.Statistics;
import com.shotFormLetter.sFL.domain.statistics.domain.repository.StatisticsRepository;
import com.shotFormLetter.sFL.domain.statistics.domain.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;


import static org.assertj.core.api.Assertions.assertThat;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SFlApplicationTests {
//    @Test
//    public void findEmoji() {
//        String name = "\uD83E\uDEE1\uD83E\uDD23\uD83E\uDEE1\uD83D\uDE02☢\uFE0F\uD83D\uDE0D☺\uFE0F\uD83E\uDD14\uD83E\uDEE1" + "최배달";
//        String emoji = "(?:[\\u2700-\\u27bf]|" +
//                "(?:[\\ud83c\\udde6-\\ud83c\\uddff]){2}|" +
//                "[\\ud800\\udc00-\\uDBFF\\uDFFF]|[\\u2600-\\u26FF])[\\ufe0e\\ufe0f]?(?:[\\u0300-\\u036f\\ufe20-\\ufe23\\u20d0-\\u20f0]|[\\ud83c\\udffb-\\ud83c\\udfff])?" +
//                "(?:\\u200d(?:[^\\ud800-\\udfff]|" +
//                "(?:[\\ud83c\\udde6-\\ud83c\\uddff]){2}|" +
//                "[\\ud800\\udc00-\\uDBFF\\uDFFF]|[\\u2600-\\u26FF])[\\ufe0e\\ufe0f]?(?:[\\u0300-\\u036f\\ufe20-\\ufe23\\u20d0-\\u20f0]|[\\ud83c\\udffb-\\ud83c\\udfff])?)*|" +
//                "[\\u0023-\\u0039]\\ufe0f?\\u20e3|\\u3299|\\u3297|\\u303d|\\u3030|\\u24c2|[\\ud83c\\udd70-\\ud83c\\udd71]|[\\ud83c\\udd7e-\\ud83c\\udd7f]|\\ud83c\\udd8e|[\\ud83c\\udd91-\\ud83c\\udd9a]|[\\ud83c\\udde6-\\ud83c\\uddff]|[\\ud83c\\ude01-\\ud83c\\ude02]|\\ud83c\\ude1a|\\ud83c\\ude2f|[\\ud83c\\ude32-\\ud83c\\ude3a]|[\\ud83c\\ude50-\\ud83c\\ude51]|\\u203c|\\u2049|[\\u25aa-\\u25ab]|\\u25b6|\\u25c0|[\\u25fb-\\u25fe]|\\u00a9|\\u00ae|\\u2122|\\u2139|\\ud83c\\udc04|[\\u2600-\\u26FF]|\\u2b05|\\u2b06|\\u2b07|\\u2b1b|\\u2b1c|\\u2b50|\\u2b55|\\u231a|\\u231b|\\u2328|\\u23cf|[\\u23e9-\\u23f3]|[\\u23f8-\\u23fa]|\\ud83c\\udccf|\\u2934|\\u2935|[\\u2190-\\u21ff]";
//
//        Pattern pattern = Pattern.compile(emoji);
//        Matcher matcher = pattern.matcher(name);
//        System.out.println(matcher.find());
//    }
//    @Test
//    public void make(){
//        String ref="[{\"type\":\"IMAGE\",\"reference\":1688702425234},{\"type\":\"IMAGE\",\"reference\":1688702482621},{\"type\":\"IMAGE\",\"reference\":1688702510658},{\"type\":\"IMAGE\",\"reference\":1688702568886},{\"type\":\"IMAGE\",\"reference\":1688702586878},{\"type\":\"IMAGE\",\"reference\":1688702606805}]";
//        List<String> list = new ArrayList<>();
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/d6f4e26c-60f8-48e1-a3ad-e926988d7251.jpg");
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/9ddad009-bd54-4e67-b4f7-6b8e300009cd.jpg");
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/c2de78ba-2b0e-469b-886f-128c5be27c3c.jpg");
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/382f19e4-43d1-4427-b218-0c1914e8cd49.jpg");
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/29ea28f1-ec3e-4d94-9e1c-45492ba10717.jpg");
//        list.add("https://shotformletter-media-server.s3.ap-northeast-2.amazonaws.com/2/66/images/825ff003-d6f3-420a-a08c-295c5f4bff3e.gif");
//
//
//
//        List<MediaDto> MediaDtos=new ArrayList<>();
//        JSONArray jsonArray = new JSONArray(ref);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        for(int i =0; i<jsonArray.length(); i++){
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            String url = list.get(i);;
//            jsonObject.put("s3url", url);
//            MediaDto mediaDto=objectMapper.readValue(jsonObject.toString(),MediaDto.class);
//            MediaDtos.add(mediaDto);
//        }
//    }

//    @Test
//    public void test(){
//        String now_reference = "[{\"type\":\"VIDEO\",\"reference\":\"a\"},{\"type\":\"IMAGE\",\"reference\":\"b\"},{\"type\":\"IMAGE\",\"reference\":\"c\"},{\"type\":\"IMAGE\",\"reference\":\"d\"}]";
//        String deleteList = "[a,b]";
//
//        // Convert now_reference to JSONArray
//        JSONArray jsonArray = new JSONArray(now_reference);
//
//        // Convert deleteList to JSONArray
//        JSONArray deleteArray = new JSONArray(deleteList);
//
//        List<String> alist = new ArrayList<>();
//        alist.add("a");
//        alist.add("b");
//        alist.add("c");
//        alist.add("d");
//        // Remove references from now_reference based on deleteList
//        for (int i = 0; i<jsonArray.length(); i++) {
//            JSONObject item = jsonArray.getJSONObject(i);
//            if (deleteArray.toList().contains(item.getString("reference"))) {
//                jsonArray.remove(i);
//                alist.remove(i);
//            }
//        }
//
//        // Convert JSONArray back to string
//        String updatedNowReference = jsonArray.toString();
//        System.out.println(alist);
//        System.out.println(updatedNowReference);
//    }
//
//    @Test
//    public void test(){
//        String now_reference = "[{\"type\":\"VIDEO\",\"reference\":1},{\"type\":\"IMAGE\",\"reference\":2},{\"type\":\"IMAGE\",\"reference\":3},{\"type\":\"IMAGE\",\"reference\":4}]";
//        String new_media_reference = "[{\"type\":\"IMAGE\",\"reference\":5},{\"type\":\"IMAGE\",\"reference\":6}]";
//        String list = "[\"1\",\"2\"]";
//        List<String> alist = new ArrayList<>();
//        alist.add("1");
//        alist.add("2");
//        alist.add("3");
//        alist.add("4");
//
//        // now_reference를 JSONArray로 변환
//        JSONArray nowReferenceArray = new JSONArray(now_reference);
//        // new_media_reference를 JSONArray로 변환
//        JSONArray newMediaReferenceArray = new JSONArray(new_media_reference);
//        JSONArray listArray = new JSONArray(list);
//
//        try {
//            // new_media_reference의 요소들을 now_reference에 추가
//            for (int i = 0; i < newMediaReferenceArray.length(); i++) {
//                nowReferenceArray.put(newMediaReferenceArray.getJSONObject(i));
//            }
//        } catch (JSONException e) {
//            System.out.println("실패");
//        }
//
//        System.out.println(nowReferenceArray.toString());
//
//        List<Long> deleteList = convertToList(listArray);
//
//        for (int i = nowReferenceArray.length() - 1; i >= 0; i--) {
//            try {
//                JSONObject item = nowReferenceArray.getJSONObject(i);
//                long ref = item.getLong("reference");
//                if (deleteList.contains(ref)) {
//                    nowReferenceArray.remove(i);
//                    alist.remove(i);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        String modifiedNowReference = nowReferenceArray.toString();
//        System.out.println(modifiedNowReference);
//        System.out.println(alist);
//    }
//
//    private static List<Long> convertToList(JSONArray jsonArray) {
//        List<Long> list = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            try {
//                long value = jsonArray.getLong(i);
//                list.add(value);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return list;
//    }

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private StatisticsService statisticsService;
    @Test
    public void testTimeline(){
        List<String> timeLine = new ArrayList<>();
        for(int i=0; i<10; i++){
            timeLine.add(LocalDate.now().toString());
        }
        System.out.println(timeLine);

        List<LocalDate> localLine =new ArrayList<>();

        for(String time:timeLine){
            localLine.add(LocalDate.parse(time).plusDays(10));
        }
        System.out.println(localLine);
    }

    @Test
    @Transactional
    public void weekTimeLineSet(){
//        LocalDate weektime=LocalDate.now();

        String date="2023-07-24";
        LocalDate weektime=LocalDate.parse(date);

        LocalDate firstDayOfMonth = weektime;
        LocalDate chek=firstDayOfMonth;
        LocalDate reference=weektime;

        if(weektime.getDayOfMonth()==1){
            firstDayOfMonth=weektime.minusMonths(1);
            chek=firstDayOfMonth;
            reference=weektime.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        } else{
            firstDayOfMonth=weektime.minusDays(weektime.getDayOfMonth()-1);
            chek=firstDayOfMonth;
        }
        Integer week=1;


        while(chek.isBefore(reference) || chek==reference){
            DayOfWeek dayOfWeek=chek.getDayOfWeek();

            if(dayOfWeek.equals(DayOfWeek.MONDAY)){
                LocalDate last=chek.plusDays(6);
                System.out.println(week+"번째 주의 시작과 끝");
                System.out.println(chek);
                System.out.println(last);
                week = week+1;
                chek=firstDayOfMonth.plusDays(6);
                firstDayOfMonth=firstDayOfMonth.plusDays(6);
            }


            chek=firstDayOfMonth.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
            LocalDate last=chek.plusDays(6);
            System.out.println(week+"번째 주의 시작과 끝");
            System.out.println(chek);
            System.out.println(last);
            week = week+1;
            chek=firstDayOfMonth.plusDays(6);
            firstDayOfMonth=firstDayOfMonth.plusDays(6);

        }


    }


    @Test
    @Transactional
    public void monthLineSet(){
        String date="2022-01-03";

        LocalDate localDate=LocalDate.parse(date);
        LocalDate start=localDate;
        Integer during=0;
        if(localDate.getMonthValue()==1){
            start=localDate.minusYears(1);
            during =12;
        } else {
            start=localDate.minusMonths(localDate.getMonthValue()-1);
            during = localDate.getMonthValue()-1;
        }

        List<String> a=new ArrayList<>();

        for(int i=0; i<during; i++){
            LocalDate getTime=start.plusMonths(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String month= getTime.format(formatter);
            a.add(month);
        }

        System.out.println(a);
    }

    @Test
    @Transactional
    public void timeLineSet(){
        String date="2020-03-01";

        LocalDate localDate=LocalDate.parse(date);


        LocalDate start=localDate;

        Integer during=0;

        if(localDate.getDayOfMonth()==1){
            start=localDate.minusMonths(1);
            during=localDate.minusDays(1).getDayOfMonth();
        }else{
            start=localDate.minusDays(localDate.getDayOfMonth()-1);
            during=localDate.getDayOfMonth()-1;
        }

        for(int i=0; i<during; i++){
            System.out.println(start.plusDays(i).toString());
        }


        System.out.println(start.toString());
        System.out.println(during);

    }

    @Test
    @Transactional
    public void testLine(){
        Long meberId=1L;

        List<Statistics> test = statisticsRepository.getListByMemberId(meberId);

        List<String> aa =new ArrayList<>();

        for(Statistics s : test){
            List<String> monthLane=s.getViewTime();
            for(String mt:monthLane){
                LocalDateTime dateTime=LocalDateTime.parse(mt);
                DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM");
                String month=dateTime.format(formatter);
                aa.add(month);
            }
        }
//
        System.out.println(aa);

        LocalDate timeLane=LocalDate.now();

        Integer start=timeLane.getMonthValue()-1;

        LocalDate monthLane=timeLane.minusMonths(start);

        List<String> bb= new ArrayList<>();

        List<Integer> cc= new ArrayList<>();
        for(int i=0; i<start; i++){
            LocalDate userMonthTimeLane=monthLane.plusMonths(i);
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM");
            String month=userMonthTimeLane.format(formatter);
            bb.add(month);
            Integer cnt= Collections.frequency(aa,month);
            cc.add(cnt);
        }

        System.out.println(bb);

        System.out.println(cc);
//
//
//        List<String> aa= new ArrayList<>();
//        for(Statistics s : test){
//            List<String> timeLane = s.getViewTime();
//            for (int i=0; i<timeLane.size(); i++){
//                LocalDateTime dateTime=LocalDateTime.parse(timeLane.get(i));
//                aa.add(dateTime.toLocalDate().toString());
//            }
//        }
//        System.out.println("추출결과");
//        System.out.println(aa);
//
//        List<String> date = new ArrayList<>();
//        List<Integer> count = new ArrayList<>();
//
//        String time="2023-07-24";
//
//        LocalDate localDate=LocalDate.parse(time);
//
//        Integer timeLine=localDate.getDayOfMonth()-1;
//
//        LocalDate startTime=localDate.minusDays(timeLine);
//
//        for(int i=0; i<timeLine; i++){
//            LocalDate getTime=startTime.plusDays(i);
//            date.add(getTime.toString());
//        }
//        System.out.println("타임 라인");
//        System.out.println(date);
//
//        for(String s: date){
//            System.out.println(s);
//            Integer cnt = Collections.frequency(aa,s);
//            System.out.println(cnt);
//            count.add(cnt);
//        }
//
//
//        System.out.println("타임 라인 에 대한 조회수");
//        System.out.println(count);

    }




}

