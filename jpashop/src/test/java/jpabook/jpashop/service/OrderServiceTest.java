package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired private EntityManager em;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, getOrder.getOrderItemList().size(), "상품 갯수는 1개");
        Assertions.assertEquals(10000*orderCount, getOrder.getTotalPrice(), "전체 가격은 20000");
        Assertions.assertEquals(8, book.getStockQuantity(), "상품 주문시 재고가 8개로 감소");
    }



    @Test
    public void 주문취소() throws Exception{
        Member member = createMember();
        Item item = createBook("시골JPA",10000,10);
        int orderCount =2;
        Long orderId = orderService.order(member.getId(), item.getId(),orderCount);

        orderService.cancelOrder(orderId);

        Order order = orderRepository.findOne(orderId);
        Assertions.assertEquals(order.getStatus(),OrderStatus.CANCEL,"주문 상태 일치");
        assertEquals(10, item.getStockQuantity(),"기존 수량 일치");
    }

    @Test()
    public void 상품주문_재고수량초과() throws Exception{

        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 12;

        NotEnoughStockException e = Assertions.assertThrows(NotEnoughStockException.class, ()->{
            orderService.order(member.getId(), item.getId(), orderCount);
        });

        Assertions.assertEquals(e.getMessage(),"need more stock");

    }

    private Book createBook(String name, int price, int count) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(count);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강남","123-123"));
        em.persist(member);
        return member;
    }
}