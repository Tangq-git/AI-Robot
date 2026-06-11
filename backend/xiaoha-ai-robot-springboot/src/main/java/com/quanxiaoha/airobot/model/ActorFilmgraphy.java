package com.quanxiaoha.airobot.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * 演员-电影集合
 * @param actor
 * @param movies
 */
@JsonPropertyOrder({"actor", "movies"})
public record ActorFilmgraphy(String actor, List<String> movies) {
}
