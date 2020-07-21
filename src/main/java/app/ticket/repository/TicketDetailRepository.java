package app.ticket.repository;

import app.ticket.entity.TicketDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketDetailRepository extends MongoRepository<TicketDetail,
        ObjectId> {
    TicketDetail findByTid(Integer tid);
}
