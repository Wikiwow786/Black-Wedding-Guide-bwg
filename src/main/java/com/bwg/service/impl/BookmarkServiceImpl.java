package com.bwg.service.impl;

import com.bwg.domain.Bookmarks;
import com.bwg.domain.QBookmarks;
import com.bwg.exception.ResourceNotFoundException;
import com.bwg.model.AuthModel;
import com.bwg.model.BookmarkModel;
import com.bwg.repository.BookmarkRepository;
import com.bwg.service.BookmarkService;
import com.querydsl.core.BooleanBuilder;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.bwg.logger.Logger.format;
import static com.bwg.logger.Logger.info;
import static com.bwg.logger.LoggingEvent.LOG_SERVICE_OR_REPOSITORY;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Override
    public Page<BookmarkModel> getAllBookmarks(String search, AuthModel authModel, Pageable pageable) {
        BooleanBuilder filter = new BooleanBuilder();
        if(StringUtils.isNotBlank(search)){
         filter.and(QBookmarks.bookmarks.title.containsIgnoreCase(search));
        }
        return bookmarkRepository.findAll(filter,pageable).map(BookmarkModel::new);
    }

    @Override
    public List<BookmarkModel> getUserBookmarks(Long userId, AuthModel authModel) {
        return bookmarkRepository.findAllByUserId(userId).stream().map(BookmarkModel::new).toList();
    }

    @Override
    public BookmarkModel createBookmark(BookmarkModel bookmarkModel, AuthModel authModel) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Creating Bookmark ..."), this);
        return new BookmarkModel(bookmarkRepository.save(assemble(bookmarkModel,authModel)));
    }

    @Override
    public BookmarkModel updateBookmark(Long bookmarkId, BookmarkModel bookmarkModel) {
        return null;
    }

    @Override
    public void deleteBookmark(Long bookmarkId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Deleting Bookmark ", bookmarkId), this);
        Bookmarks bookmarks = bookmarkRepository.findById(bookmarkId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));
        bookmarkRepository.delete(bookmarks);
    }

    @Override
    @Transactional
    public void deleteUserBookmarks(Long userId) {
        info(LOG_SERVICE_OR_REPOSITORY, format("Deleting User Bookmark of id ", userId), this);
        bookmarkRepository.deleteAllByUserId(userId);
        info(LOG_SERVICE_OR_REPOSITORY, format("Deleted bookmarks of user with id  ", userId), this);
    }

    private Bookmarks assemble(BookmarkModel bookmarkModel, AuthModel authModel){
        Bookmarks bookmarks = new Bookmarks();
        BeanUtils.copyProperties(bookmarkModel, bookmarks);
        bookmarks.setCreatedAt(OffsetDateTime.now());
        return bookmarks;
    }
}
