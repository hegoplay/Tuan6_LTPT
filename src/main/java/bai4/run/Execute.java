package bai4.run;

import java.security.KeyPair;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import bai4.dao.CollectionsList;
import bai4.dao.CustomerDAO;
import bai4.dao.OrderDAO;
import bai4.dao.ProductDAO;
import bai4.dao.StaffDAO;
import bai4.entities.Address;
import bai4.entities.Customer;
import bai4.entities.Order;
import bai4.entities.OrderDetail;
import bai4.entities.OrderStatus;
import bai4.entities.Phone;
import bai4.entities.Product;
import bai4.entities.Staff;
import utils.ConnectMongoDB;

public class Execute {
	public static void main(String[] args) {
		ConnectMongoDB.getConnection("BikeStores");
		OrderDAO.getCollections();
		StaffDAO.getCollections();
		CustomerDAO.getCollections();
		ProductDAO.getCollections();
//		addOrder();
//		test cau 1
//		cau1();
//		cau2();
//		cau3();
//		cau4();
//		cau5();
//		cau6();
//		cau7();
//		cau8();
//		cau9();
//		cau10();
//		cau11();
//		cau12();
		cau13();
//		cau14();
//		cau15();
		ConnectMongoDB.closeClient();
	}
	
	private static void cau1() {
		Order o = OrderDAO.getOrderById(new ObjectId("65eec1c9a638dd2ccb2d1644"));
		System.out.println(o);
	}
//	2. Tìm danh sách sản phẩm có giá cao nhất.
	private static void cau2() {
		List<Product> list = ProductDAO.getProductsWithAggregate(Arrays.asList(
				Aggregates.group(
						null, 
						Accumulators.addToSet("val", "$$ROOT"),
						Accumulators.max("maxPrice","$price")),
				Aggregates.unwind("$val"),
				Aggregates.match(Filters.expr(Document.parse("{$eq:[\"$val.price\",\"$maxPrice\"]}"))),
				Aggregates.replaceRoot("$val")));
		for(Product p :list) {
			System.out.println(p);
		}
	}
//	3. Tìm danh sách sản phẩm chưa bán được lần nào.
	private static void cau3() {
//		 db.products.aggregate([{$lookup:{from:"orders",localField:"_id",foreignField:"order_details.product_id",as:"order",pipeline:[{$unwind:"$order_details"},{$replaceRoot:{newRoot:"$order_details"}}]}},{$match:{$expr:{$eq:[{$size:"$order"},0]}}}])
//		db.products.aggregate([{$lookup:{from:"orders",let:{"pid":"$_id"},pipeline:[{$unwind:"$order_details"},{$replaceRoot:{newRoot:"$order_details"}},{$match:{$expr:{$eq:["$product_id","$$pid"]}}}],as:"orders"}},{$match:{$expr:{$eq:[{$size:"$orders"},0]}}}])
		List<Product> list = ProductDAO.getProductsWithAggregate(Arrays.asList(
				Document.parse("{$lookup:{from:\"orders\",let:{\"pid\":\"$_id\"},pipeline:[{$unwind:\"$order_details\"},{$replaceRoot:{newRoot:\"$order_details\"}},{$match:{$expr:{$eq:[\"$product_id\",\"$$pid\"]}}}],as:\"orders\"}}"),
				Aggregates.match(Filters.expr(Document.parse("{$eq:[{$size:\"$orders\"},0]}")))));
		for(Product p :list) {
			System.out.println(p);
		}
	}
//	4. Thống kê số khách hàng theo từng bang.
//	+ getNumberCustomerByState() : Map<String, Integer>
	private static Map<String,Integer> getNumberCustomerByState(){
		 Map<String,Integer> res = new HashMap<>();
		 CustomerDAO.getDocsWithAggregate(Arrays.asList(
				 Aggregates.group("$address.state", Accumulators.sum("cnt", 1)))).
		 forEach(x-> res.put(x.getString("_id"), x.getInteger("cnt")));
		 return res;
	}
	private static void cau4() {
		Map<String,Integer> res = getNumberCustomerByState();
		System.out.println(res);
	}
	
//	5. Tính tổng tiền của đơn hàng khi biết mã số đơn hàng.
	private static void cau5() {
		Order o = OrderDAO.getOrderById(new ObjectId("615279c5dc90aa2be71fd90c"));
		System.out.println(o.getOrderTotal());
	}
//	6. Đếm số đơn hàng của từng khách hàng.
//	+ getOrdersByCustomers() : Map<Customer, Integer>
	private static Map<Customer,Integer> getOrdersByCustomers(){
		Map<Customer,Integer> res = new HashMap<>();
		OrderDAO.getDocsWithAggregate(Arrays.asList(
				 Aggregates.group("$customer.customer_id", Accumulators.sum("cnt", 1)),
				 Aggregates.sort(Sorts.descending("cnt")))).
		forEach(x-> res.put(CustomerDAO.getCustomerById(x.getString("_id")), x.getInteger("cnt")));
		return res;
	}
	private static void cau6() {
		Map<Customer,Integer> res = getOrdersByCustomers();
		for(Map.Entry<Customer,Integer> x : res.entrySet()) {
			System.out.println(x);
		}
	}
//	7. Tính tổng số lượng của từng sản phẩm đã bán ra.
//	+ getTotalProduct(): Map<Product, Integer>
	private static Map<Product,Integer>  getTotalProduct(){
		Map<Product,Integer> res = new HashMap<>();
		ProductDAO.getDocsWithAggregate(Arrays.asList(
				Document.parse("{$lookup:{from:\"orders\",let:{\"pid\":\"$_id\"},pipeline:[{$unwind:\"$order_details\"},{$replaceRoot:{newRoot:\"$order_details\"}},{$match:{$expr:{$eq:[\"$product_id\",\"$$pid\"]}}}],as:\"orders\"}}"),
				Aggregates.set(new Field("count",Document.parse("{$sum:\"$orders.quantity\"}}"))))).
				forEach(x -> {
					res.put(ProductDAO.getProuctById(x.getLong("_id")), x.getInteger("count"));
				});;
		
		return res;
	}
	private static void cau7() {
		Map<Product,Integer> res = getTotalProduct();
		for(Map.Entry<Product,Integer> x : res.entrySet()) {
			System.out.println(x);
		}
	}
//	8. Dùng text search để tìm kiếm sản phẩm theo tên sản phẩm.\
	private static void cau8() {
//		khoi tao index text o field name
		List<Product> list = ProductDAO.getProductsWithCondition(Filters.text("Electra"));
		for(Product x : list) {
			System.out.println(x);
		}
	}
//	9. Tính tổng tiền của tất cả các hóa đơn trong một ngày nào đó.
	private static void cau9() {
		LocalDate d = LocalDate.of(2021, 9, 28);
		LocalDate end = d.plusDays(1);
//		db.orders.find({$expr:{$and:[{$gt:["$order_date",ISODate("2021-09-28")]},{$lt:["$order_date",ISODate("2021-09-29")]}]}})
		String query = "{$and:[{$gt:[\"$order_date\",ISODate(\""
						+d.getYear()+"-" +d.getMonth()+"-"
						+d.getDayOfMonth()+"\")]},"
						+ "{$lt:[\"$order_date\",ISODate(\""+
						end.getYear()+"-"+end.getMonth() +"-" +
						end.getDayOfMonth() + "\")]}]}";
		List<Document> o = OrderDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.match(
						Filters.expr(
								query))));
		double total = 0;
		for(Document doc : o) {
			total+= doc.getDouble("order_total");
		}
		System.out.println(total);
	}
//	10. Thêm dữ liệu vào từng collection.
	private static void cau10() {
//		addOrder();
		Customer c = new Customer("IUH_K15", "Trung", "Phan", Arrays.asList(new Phone("personal", "0376626025")), "phantrungchi@gmail.com", Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		c.setAddress(new Address("TDC", "Sa Dec", "Mai Chi Tho", 71200));
		CustomerDAO.insertOne(c);
		Staff f = new Staff(11, "manh"	, "pham", new Phone("home", "0944713015"), "pmanhh19@gmail.com");
		f.setManager(StaffDAO.getStaffById(1));
		StaffDAO.insertOne(f);
		ProductDAO.insertOne(new Product(322, "Honda", "bike", "Honda AirBlade 125", Arrays.asList("blue","black"), 2023, 2250));
	
	}
//	11. Cập nhật giá của sản phẩm khi biết mã sản phẩm.
	private static void cau11() {
		long id = 20;
		double price = 600;
//		Document doc = new Document();
//		doc.append("$price", price);
		ProductDAO.findOneAndUpdate(Filters.eq("_id", id),Aggregates.set(new Field("price",price)) );
	}
//	12. Xóa tất cả các khách hàng chưa mua hàng.
	private static void cau12() {
		List<Document> list = CustomerDAO.getDocsWithAggregate(Arrays.asList(
				Document.parse("{$lookup:{from:\"orders\",localField:\"_id\",foreignField:\"customer.customer_id\",pipeline:[{$replaceRoot:{newRoot:\"$customer\"}}],as:\"order\"}}"),
				Aggregates.match(
						Filters.expr (
								Filters.eq("$eq",Arrays.asList(new Document("$size", "$order"),0))))));
		for(Document doc : list) {
			CustomerDAO.deleteOne(Filters.eq("_id", doc.getString("_id")));
		}
	}
//	13. Danh sách các khách hàng có từ 2 số điện thoại trở lên
	private static void cau13() {
		List<Document> list = CustomerDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.match(Filters.expr(Filters.eq("$eq", Arrays.asList(new Document("$type", "$phones"),"array")))),
//				Document.parse("{$match:{$expr:{$eq:[{ $type : \"$phones\"},\"array\"]}}}"),
				Document.parse("{$match:{$expr:{$gte:[{$size:\"$phones\"},1]}}}")
//				Aggregates.match(Filters.expr(Filters.gte("$gte", Arrays.asList(new Document("$size", "$phones"),2))))
				));
		for(Document doc : list) {
			System.out.println(CustomerDAO.convert2Cus(doc));
		}
	}
//	14.Thống kê tổng tiền hóa đơn theo tháng / năm.
	private static void cau14() {
		LocalDate start = LocalDate.of(2021, 9, 1);
		LocalDate end = start.plusMonths(1);
//		db.orders.find({$expr:{$and:[{$gt:["$order_date",ISODate("2021-09-28")]},{$lt:["$order_date",ISODate("2021-09-29")]}]}})
		String query = "{$and:[{$gt:[\"$order_date\",ISODate(\""
						+start.getYear()+"-" +start.getMonth()+"-"
						+start.getDayOfMonth()+"\")]},"
						+ "{$lt:[\"$order_date\",ISODate(\""+
						end.getYear()+"-"+end.getMonth() +"-" +
						end.getDayOfMonth() + "\")]}]}";
		List<Document> o = OrderDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.match(
						Filters.expr(
								query))));
		double total = 0;
		for(Document doc : o) {
			total+= doc.getDouble("order_total");
		}
		System.out.println("Tong tien thang 9/2021: " + total);
	}
//	15. Tìm khách hàng khi biết số điện thoại
	private static void cau15() {
		String number = "(281) 926-8010";
		Document doc = new Document();
		doc.append("phones.number", number);
		List<Customer> list = CustomerDAO.getCustomerWithCondition(doc);
		System.out.println(list.get(0));
	}
	
	private static void addOrder() {
		Order o = new Order();
		o.setAddress(new Address("TDC", "HCMC", "Le Van Viet", 71000));
		o.setStaff(
				StaffDAO.
					findStaffWithCondition(
							Filters.eq("_id", 2)).
					get(0));;
		o.setCustomer(
				CustomerDAO.
					getCustomerWithCondition(
						Filters.eq("_id", "RAND923")).
					get(0));
		o.setOrderDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		o.setShippedDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		OrderDetail od1 = new OrderDetail();
		od1.setColor("red");
		od1.setDiscount(0.05);
		Product p1 = ProductDAO.getProductsWithCondition(
				Filters.eq("_id",10)).get(0);
		od1.setPrice(p1.getPrice());
		od1.setProduct(p1);
		od1.setQuantity(2);
		od1.setPrice(0);
		o.setOrderDetails(Arrays.asList(od1));
		
		o.setOrderID(new ObjectId().toHexString());
		o.setOrderStatus(OrderStatus.COMPLETED);
//		o.setShippedDate(null);
		o.setStaff(StaffDAO.findStaffWithCondition(Filters.eq("_id",1)).get(0));
		System.out.println(OrderDAO.insertOne(o));
//		System.out.println(o);
//		System.out.println(OrderDAO.Order2Doc(o));
	}
}
