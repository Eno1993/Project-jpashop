package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {


    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();

    //비즈니스 로직 추가 -> 데이터를 가지고 있는 곳에서 비지니스 로직을 만드는 것이 좋음
    //객체 지향적 성격 항샹과 응집력 향상

    //재고수량 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    //재고수량 감소
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock <0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
