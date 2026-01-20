package com.emotions.emotions.helpers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.Paginator;

@Service
public class PaginatorController {
    
    public Paginator generatePaginator(Page<?> page) {
        Paginator paginator = new Paginator();

        paginator.setTotalPages(page.getTotalPages());
        paginator.setPrevDots(false);
        paginator.setNextDots(false);

        int totalPages = paginator.getTotalPages();
        
        if(totalPages > 1) {
            int currentPage = page.getNumber();
            int end = 0;
            int start = 0;

            if (totalPages <= 4) {
                start = 1;
                end = totalPages;

                paginator.setPrevDots(false);
                paginator.setNextDots(false);
            }
            else if(totalPages >= 5 && currentPage < 3) {
                end = 4;
                start = 1;

                paginator.setPrevDots(false);
                paginator.setNextDots(true);
            } else if (totalPages >= 5 && currentPage > totalPages - 4) {
                start = totalPages - 3;
                end = totalPages;

                paginator.setPrevDots(true);
                paginator.setNextDots(false);
            } else {
                start = currentPage;
                end = Math.min(currentPage + 2, totalPages);

                paginator.setPrevDots(true);
                paginator.setNextDots(true);
            }

            List<Integer> pageNumbers = new ArrayList<>();
            for(int i = start; i <= end; i++) {
                pageNumbers.add(i);
            }

            paginator.setPageNumbers(pageNumbers);
        }

        return paginator;
    }
}
