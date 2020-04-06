drop procedure if exists insert_sales_transactions;
delimiter //
create procedure insert_sales_transactions (in email varchar(50), in token varchar(50))
begin
    insert into sales (email, movieId, quantity, saleDate)
    select carts.email, carts.movieId, carts.quantity, current_date
    from carts
    where carts.email = email;
    insert ignore into transactions (sId, token)
    select sales.id, token
    from sales
    where email = email;
end//
delimiter ;
