package com.shotFormLetter.sFL;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shotFormLetter.sFL.domain.post.controller.PostController;
import com.shotFormLetter.sFL.domain.post.domain.dto.MediaDto;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.InstanceOfAssertFactories.atomicIntegerFieldUpdater;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.assertj.core.api.Assertions.assertThat;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

}
