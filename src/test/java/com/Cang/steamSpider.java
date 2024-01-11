package com.Cang;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description TODO
 * @DateTime 2024/1/4 17:42
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class steamSpider {



    public static List<String> getCarouselImageUrls(String steamGameUrl) {
        List<String> carouselImageUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(steamGameUrl).timeout(5000).get();
            Elements imageElements = doc.select("div.screenshot_holder a[href]");

            for (Element imageElement : imageElements) {
                String imageUrl = imageElement.attr("href");
                carouselImageUrls.add(imageUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return carouselImageUrls;
    }

    @Test
    public void getPic() {
        String steamGameUrl = "https://store.steampowered.com/app/255710/";
        List<String> carouselImageUrls = getCarouselImageUrls(steamGameUrl);
        for (String imageUrl : carouselImageUrls) {
            String realPicLink = imageUrl.replace("https://steamcommunity.com/linkfilter/?u=", "");
            System.out.println(realPicLink);
        }


    }
}
