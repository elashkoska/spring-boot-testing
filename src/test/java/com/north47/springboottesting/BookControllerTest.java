package com.north47.springboottesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.north47.springboottesting.models.Book;
import com.north47.springboottesting.repository.BookRepository;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Book DEFAULT_BOOK = new Book(null, "Asimov", "Foundation", 350);

    @Test
    public void testShouldReturnCreatedWhenValidBook() throws Exception {
        when(repository.save(Mockito.any())).thenReturn(DEFAULT_BOOK);

        this.mockMvc.perform(post("/books")
                .content(objectMapper.writeValueAsString(DEFAULT_BOOK))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(DEFAULT_BOOK.getName()));
    }

    @Test
    public void testShouldFindBooksWhenExists() throws Exception {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(DEFAULT_BOOK));

        this.mockMvc.perform(get("/books/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.is(objectMapper.writeValueAsString(DEFAULT_BOOK))));
    }

    @Test
    public void testShouldReturn404WhenBookMissing() throws Exception {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/books/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
