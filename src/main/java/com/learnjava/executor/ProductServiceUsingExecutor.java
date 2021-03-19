package com.learnjava.executor;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;

import java.util.concurrent.*;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

public class ProductServiceUsingExecutor {

    static ExecutorService executorService22 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ProductInfoService productInfoService;
    private ReviewService reviewService;

    public ProductServiceUsingExecutor(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) throws ExecutionException, InterruptedException, TimeoutException {
        stopWatch.start();

       Future<ProductInfo> productInfoFuture =  executorService22.submit(()-> productInfoService.retrieveProductInfo(productId));
        Future<Review> reviewFuture =  executorService22.submit(()-> reviewService.retrieveReviews(productId));

  //      ProductInfo productInfo =  productInfoFuture.get();      // if some error happens in the call then we will infinitely
        ProductInfo productInfo =  productInfoFuture.get(1, TimeUnit.SECONDS); // as we put 1-Sec  means if the result does
        Review  review = reviewFuture.get();                                         // not come in 1-Sec then code execute further
                                                                                   // without waiting for this result.
        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return new Product(productId, productInfo, review );
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingExecutor productService = new ProductServiceUsingExecutor(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);

    }
}
