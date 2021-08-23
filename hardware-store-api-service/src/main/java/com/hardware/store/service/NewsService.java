package com.hardware.store.service;

import com.hardware.store.domain.News;
import com.hardware.store.dto.NewsDTO;
import com.hardware.store.exception.EntityNotFoundException;
import com.hardware.store.mapper.NewsMapper;
import com.hardware.store.repository.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class NewsService {

    private final NewsRepository newsRepository;

    private final NewsMapper newsMapper;

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("News with ID : " + id + " not found!"));
    }

    public News save(NewsDTO newsDTO) {
        return newsRepository.save(newsMapper.fromDTO(newsDTO));
    }

    public News save(News news) {
        return newsRepository.save(news);
    }

    public News update(Long id, NewsDTO newsDTO) {
        News news = newsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("News with ID : " + id + " not found!"));
        newsMapper.fromDTO(newsDTO, news);
        return newsRepository.save(news.updated());
    }

    public void delete(Long id) {
        newsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("News with ID : " + id + " not found!"));
        newsRepository.deleteById(id);
    }
}
