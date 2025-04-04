package com.bwg.service;

import com.bwg.domain.Bookmarks;
import com.bwg.model.AuthModel;
import com.bwg.model.BookmarkModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookmarkService {

    Page<BookmarkModel> getAllBookmarks(String search, AuthModel authModel, Pageable pageable);

    List<BookmarkModel> getUserBookmarks(Long userId, AuthModel authModel);

    BookmarkModel createBookmark(BookmarkModel bookmark, AuthModel authModel);

    BookmarkModel updateBookmark(Long bookmarkId, BookmarkModel bookmarkModel);

    void deleteBookmark(Long bookmarkId);

    void deleteUserBookmarks(Long userId,AuthModel authModel);
}
