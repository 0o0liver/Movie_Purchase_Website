package edu.uci.ics.binghal.service.billing.core;

import java.net.URI;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import edu.uci.ics.binghal.service.billing.BillingService;
import edu.uci.ics.binghal.service.billing.logger.ServiceLogger;
import edu.uci.ics.binghal.service.billing.models.*;

import javax.ws.rs.core.UriBuilder;


public class Order {
    private static final String clientId = "AbdEnU5G5Al8HZg1PUfSyAAQoIAdmaPtAU5sKOjz9Uel7rqcjlI5z1BxSaS0si_VbJvmAuVKgqcCaJm7";
    private static final String clientSecret = "EGxHkZIMK03Wnydw-clD5DE-pEFcEoouHOjIEUolu3dXolVRJSxzy1R91is6u8bEGukQ66sdbDALAxei";

    public static OrderPlaceResponseModel place(EmailRequestModel requestModel){
        ServiceLogger.LOGGER.info("Placing order for " + requestModel.getEmail());
        if (customerNotExist(requestModel.getEmail())){
            return new OrderPlaceResponseModel(332, "Customer does not exist.", null, null);
        }
        else if (customerNoCart(requestModel.getEmail())){
            return new OrderPlaceResponseModel(341, "Shopping cart for this customer not found.", null, null);
        }
        String total = getTotal(requestModel.getEmail());
        String[] paymentInfo = createPayment(total);
        if (paymentInfo == null){
            return new OrderPlaceResponseModel(342, "Create payment failed.", null, null);
        }
        try{
            CallableStatement cStmt = BillingService.getCon().prepareCall("{call insert_sales_transactions (?, ?)}");
            cStmt.setString(1, requestModel.getEmail());
            cStmt.setString(2, paymentInfo[1]);
            cStmt.execute();
            Cart.clear(requestModel);
            return new OrderPlaceResponseModel(3400, "Order placed successfully.", paymentInfo[0], paymentInfo[1]);
        } catch (SQLException e){
            e.printStackTrace();
            return new OrderPlaceResponseModel(-1, "Internal Server Error.", null, null);
        }
    }

    public static GeneralResponseModel complete(String paymentId, String token, String PayerID){
        ServiceLogger.LOGGER.info("Completing order for token " +  token);
        if (!existToken(token)){
            return new GeneralResponseModel(3421, "Token not found.");
        }
        try{
            completePayment(paymentId, token, PayerID);
            return new GeneralResponseModel(3420, "Payment is completed successfully.");
        } catch (Exception e){
            e.printStackTrace();
            return new GeneralResponseModel(3422, "Payment can not be completed.");
        }
    }

    public static OrderRetrieveResponseModel retrieve(EmailRequestModel requestModel){
        ServiceLogger.LOGGER.info("Retrieving order for " + requestModel.getEmail());
        if (customerNotExist(requestModel.getEmail())){
            return new OrderRetrieveResponseModel(332, "Customer does not exist.", null);
        }
        String[] transactionIdList = getIds(requestModel.getEmail());
        //for (int i = 0; i < transactionIdList.length; ++i){
        //    System.out.println(transactionIdList[i]);
        //}
        transactions[] transactionsList = generateTransList(transactionIdList, requestModel.getEmail());

        return new OrderRetrieveResponseModel(3410, "Orders retrieved successfully.", transactionsList);
    }

    private static transactions[] generateTransList(String[] transactionIdList, String email){
        ServiceLogger.LOGGER.info("Generating list of transaction models from list of transaction ids.");
        List<transactions> retArrayList = new ArrayList<transactions>();
        for (int i = 0; i < transactionIdList.length; ++i){
            retArrayList.add(generateTran(transactionIdList[i], email));
        }
        transactions[] retList = new transactions[retArrayList.size()];
        retList = retArrayList.toArray(retList);
        return retList;
    }

    private static transactions generateTran(String transactionId, String email){
        ServiceLogger.LOGGER.info("Generating transaction model for: " + transactionId);
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
            Sale sale = Sale.get(apiContext, transactionId);
            //System.out.println(sale.getState());
            //System.out.println(sale.getAmount());
            //System.out.println(sale.getTransactionFee());
            //System.out.println(sale.getCreateTime());
            OrderModel[] order = generateOrder(transactionId, email);
            amount Amount = new amount(
                    sale.getAmount().getTotal(),
                    sale.getAmount().getCurrency());
            transaction_fee TransFee = new transaction_fee(
                    sale.getTransactionFee().getValue(),
                    sale.getTransactionFee().getCurrency());
            return new transactions(
                    transactionId,
                    sale.getState(),
                    Amount,
                    TransFee,
                    sale.getCreateTime(),
                    sale.getUpdateTime(),
                    order);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static OrderModel[] generateOrder(String transactionId, String email){
        ServiceLogger.LOGGER.info("Generating list of items for: " +  transactionId);
        try{
            List<OrderModel> retArrayList = new ArrayList<OrderModel>();
            String query = "select m.movieId, quantity, saleDate, unit_price, discount from transactions t join sales s on t.sId = s.id left join movie_prices m on s.movieId = m.movieId where transactionId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, transactionId);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                retArrayList.add(new OrderModel(
                        email,
                        rs.getString("movieId"),
                        rs.getInt("quantity"),
                        rs.getFloat("unit_price"),
                        rs.getFloat("discount"),
                        rs.getDate("saleDate")));
            }
            OrderModel[] ret = new OrderModel[retArrayList.size()];
            ret = retArrayList.toArray(ret);
            return ret;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private static String[] getIds(String email){
        ServiceLogger.LOGGER.info("Generating a list of transaction ids from a email.");
        List<String> retArrayList = new ArrayList<String>();
        try{
            String query = "select distinct transactionId from sales join transactions t on sales.id = t.sId where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                retArrayList.add(rs.getString("transactionId"));
            }
            String[] retList = new String[retArrayList.size()];
            retList = retArrayList.toArray(retList);
            return retList;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static void completePayment(String paymentId, String token, String PayerID){
        ServiceLogger.LOGGER.info("PayPal completing payment for " + token);
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(PayerID);
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            String transactionId = createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
            String query = "update transactions set transactionId = ? where token = ? and transactionId is null;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, transactionId);
            ps.setString(2, token);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean existToken(String token){
        ServiceLogger.LOGGER.info("Checking if a token exist.");
        try{
            String query = "select COUNT(*) from transactions where token = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, token);
            ServiceLogger.LOGGER.info("Executing: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") == 0){
                return false;
            }
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private static String[] createPayment(String total){
        ServiceLogger.LOGGER.info("Creating a PayPal payment.");
        String[] ret = new String[2];
        Amount amount = new Amount();
        amount.setCurrency("USD");
        System.out.println(total);
        amount.setTotal(total);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();

        URI baseUrl = UriBuilder.fromUri(
                BillingService.getConfigs().getScheme() +
                        BillingService.getConfigs().getHostName() +
                        BillingService.getConfigs().getPath()).port(BillingService.getConfigs().getPort()).build();

        redirectUrls.setCancelUrl(baseUrl.toString() + "/order/cancel");
        redirectUrls.setReturnUrl(baseUrl.toString() + "/order/complete");
        payment.setRedirectUrls(redirectUrls);

        String redirectUrl = "";
        try{
            APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.create(apiContext);
            if (createdPayment != null){
                List<Links> links = createdPayment.getLinks();
                for (Links link:links){
                    if (link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
            }
            ret[0] = redirectUrl;
            String[] parameterList = redirectUrl.split("=");
            ret[1] = parameterList[parameterList.length-1];
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    private static String getTotal(String email){
        ServiceLogger.LOGGER.info("Getting the total amount for user: " + email);
        try{
            Float total = 0.0f;
            String query = "select * from carts join movie_prices on carts.movieId = movie_prices.movieId where carts.email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                total += rs.getInt("quantity") * rs.getFloat("unit_price") * rs.getFloat("discount");
            }
            Float roundedTotal = (float)Math.round(total * 100f) / 100f;
            String strTotal = String.valueOf(roundedTotal);
            return strTotal;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    private static boolean customerNotExist(String email){
        ServiceLogger.LOGGER.info("Checking if " + email + " exist in customer database.");
        try{
            String query = "select COUNT(*) from customers where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") == 0){
                return true;
            }
            return false;
        } catch (SQLException e){
            return true;
        }
    }

    private static boolean customerNoCart(String email){
        ServiceLogger.LOGGER.info("Checking if " + email + " exist in shopping cart database.");
        try{
            String query = "select COUNT(*) from carts where email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Executing: " +ps.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("COUNT(*)") == 0){
                return true;
            }
            return false;
        } catch (SQLException e){
            return true;
        }
    }
}
