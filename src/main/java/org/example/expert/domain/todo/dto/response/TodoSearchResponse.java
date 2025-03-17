package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoSearchResponse {
    private String title;
    private long userCount;
    private Integer commentCount;

    @QueryProjection
    public TodoSearchResponse(String title, long userCount, Integer commentCount) {
        this.title = title;
        this.userCount = userCount;
        this.commentCount = commentCount;
    }
}
