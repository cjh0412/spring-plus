package org.example.expert.domain.todo.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final JPAQueryFactory jpaQueryFactory;

    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();

        if(weather != null) {
            builder.and(todo.weather.eq(weather));
        }

        if(startDate != null) {
            builder.and(todo.modifiedAt.goe(startDate.atStartOfDay()));
        }

        if(endDate != null) {
            builder.and(todo.modifiedAt.loe(endDate.atTime(23,59,59)));
        }

        // 데이터 조회
        JPAQuery<Todo> query = jpaQueryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(builder)
                .orderBy(todo.modifiedAt.desc());

        // 페이징 처리
        List<Todo> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수
        long totalSize = Optional.ofNullable(jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(builder)
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, totalSize).map(todoItem -> new TodoResponse(
                todoItem.getId(),
                todoItem.getTitle(),
                todoItem.getContents(),
                todoItem.getWeather(),
                new UserResponse(todoItem.getUser().getId(), todoItem.getUser().getEmail()),
                todoItem.getCreatedAt(),
                todoItem.getModifiedAt()
        ));

    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
