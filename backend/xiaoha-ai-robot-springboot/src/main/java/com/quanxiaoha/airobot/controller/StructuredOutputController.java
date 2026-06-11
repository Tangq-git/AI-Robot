package com.quanxiaoha.airobot.controller;

import com.quanxiaoha.airobot.model.ActorFilmgraphy;
import com.quanxiaoha.airobot.model.Book;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v8/ai")
public class StructuredOutputController {

    @Resource
    private ChatClient chatClient;

    /**
     * 示例1: BeanOutputConverter - 获取演员电影作品集
     * @param name
     * @return
     */
    @GetMapping("/actor/films")
    public ActorFilmgraphy generate(@RequestParam(value = "name") String name) {
        // 一次性返回结果
        return chatClient.prompt()
                .user(u -> u.text("""
                                请为演员 {actor} 生成包含5部代表作的电影作品集,
                                只包含 {actor} 担任主演的电影，不要包含任何解释说明。
                                """)
                        .param("actor", name))
                .call()
                .entity(ActorFilmgraphy.class);
    }

    /**
     * 示例2: MapOutputConverter - 获取编程语言信息
     * @param language
     * @return
     */
    @GetMapping("/language-info")
    public Map<String, Object> getLanguageInfo(@RequestParam(value = "lang") String language) {

        String userText = """
                请提供关于编程语言 {language} 的结构化信息，包含以下字段："
                name (语言名称), "
                popularity (流行度排名，整数), "
                features (主要特性，字符串数组,中文描述), "
                releaseYear (首次发布年份). "
                不要包含任何解释说明，直接输出 JSON 格式数据。
                """;

        return chatClient.prompt()
                .user(u -> u.text(userText).param("language", language))
                .call()
                .entity(new MapOutputConverter());
    }

    @GetMapping("/city-list")
    public List<String> getCityList(@RequestParam(value = "country") String country){

        return chatClient.prompt()
                .user(u->u.text(
                        """
                        列出{country}的8个主要城市名称,
                        不要包含任何编号,解释或其他文本,直接输出城市名称列表
                        """)
                        .param("country",country))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));
        //通过 .entity() 方法指定转换器，ListOutputConverter 的功能是，
        // 将 AI 的文本输出转换为 List 集合，而 DefaultConversionService
        // 提供了基础的字符串到其他类型的转换能力。
    }

    @GetMapping("/book-info")
    public Book getBookInfo(@RequestParam(value = "name") String bookTitle){
        //使用BeanOutputConverter定义输出格式
        BeanOutputConverter<Book> converter=new BeanOutputConverter<>(Book.class);

        //提示词模板
        String template= """
                  请提供关于书籍《{bookTitle}》的详细信息：
                  1. 作者姓名
                  2. 出版年份
                  3. 主要类型（数组）
                  4. 书籍描述（不少于50字）

                  不要包含任何解释说明，直接按指定格式输出。
                  {format}                              
                """;
        //format是Spring AI内置提供的
        //创建Prompt
        PromptTemplate promptTemplate=new PromptTemplate(template);
        Prompt prompt=promptTemplate.create(Map.of(
                "bookTitle",bookTitle,
                "format",converter.getFormat()
        ));

        //创建模型并转换结果
        String result=chatClient.prompt(prompt)
                .call()
                .content();

        //结构化转换
        return converter.convert(result);
    }
}
