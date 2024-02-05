package se.magnus.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.microservices.core.review.persistence.ReviewEntity;
import se.magnus.microservices.core.review.persistence.ReviewRepository;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;
    private final ServiceUtil serviceUtil;
    public ReviewServiceImpl(ServiceUtil serviceUtil ,ReviewRepository repository,ReviewMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.mapper =mapper;
        this.repository=repository;

    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);
            LOG.debug("createReview : created a review entity: {}/{}" ,body.getProductId(),body.getReviewId());
            return mapper.entityToApi(newEntity);
        }catch (DataIntegrityViolationException dive){
throw new InvalidInputException("Duplicate Key, Product Id: " +body.getProductId()+", Review Id:"+body.getReviewId());
        }
    }

    @Override
    public List<Review> getReviews(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        List<ReviewEntity> entityList=repository.findByProductId(productId);

        List<Review> list = new ArrayList<>();
        list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/reviews response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteReviews(int productId) {

    }
}
