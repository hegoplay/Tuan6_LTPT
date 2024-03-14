package bai4.dao;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import bai4.entities.Address;
import bai4.entities.Order;
import bai4.entities.OrderDetail;
import bai4.entities.OrderStatus;
import utils.ConnectMongoDB;


public class OrderDAO {
	private static MongoCollection<Document> docCollection;

	public static void getCollections() {
		MongoDatabase db = ConnectMongoDB.getDatabase();

		docCollection = db.getCollection(CollectionsList.orders.getName());
	}

//	public static List<Document> getAllDbOrder() {
//		List<Document> res = new ArrayList<>();
//		docCollection.find().into(res);
//		return res;
//	}

	public static Document convert2Doc(Order o) {
		Document doc = new Document();
		if (o.getOrderID()!=null) {
			doc.append("_id", new ObjectId(o.getOrderID()));
		}
		doc.append("order_date", o.getOrderDate());
		doc.append("order_status", o.getOrderStatus().getStatus());
		doc.append("shipped_date", o.getShippedDate());

		Document cusDoc = new Document();
		cusDoc.append("customer_id", o.getCustomer().getCustomerId());
		cusDoc.append("full_name", o.getCustomer().getFirstName() + " " + o.getCustomer().getLastName());
		cusDoc.append("email", o.getCustomer().getEmail());

		doc.append("customer", cusDoc);

		Document staffDoc = new Document();
		staffDoc.append("staff_id", o.getStaff().getStaffId());
		staffDoc.append("full_name", o.getStaff().getFirstName() + " " + o.getStaff().getLastName());
		staffDoc.append("phone_number", o.getStaff().getPhone().getNumber());

		doc.append("staff", staffDoc);
		doc.append("order_total", o.getOrderTotal());

		Document addressDoc = new Document();
		addressDoc.append("city", o.getAddress().getCity());
		addressDoc.append("state", o.getAddress().getState());
		addressDoc.append("street", o.getAddress().getStreet());
		addressDoc.append("zip_code", o.getAddress().getZipCode());

		doc.append("shipping_address", addressDoc);

		List<Document> orderList = new ArrayList<>();
		for (OrderDetail od : o.getOrderDetails()) {
			Document odDoc = new Document();
			odDoc.append("quantity", od.getQuantity());
			odDoc.append("color", od.getColor());
			odDoc.append("product_id", od.getProduct().getProductId());
			odDoc.append("line_total", od.getLineTotal());
			odDoc.append("price", od.getPrice());
			odDoc.append("discount", od.getDiscount());
			orderList.add(odDoc);
		}
		doc.append("order_details", orderList);

		return doc;
	}

	public static Order convert2Order(Document doc	) {
		Order o = new Order();
		o.setOrderID(doc.getObjectId("_id").toHexString());
		o.setOrderDate(doc.getDate("order_date"));
		String ordSt = doc.getString("order_status");
		o.setOrderStatus(getOrderStatus(ordSt));
		o.setShippedDate(doc.getDate("shipped_date"));
		
		Document cusDoc = doc.get("customer", Document.class);
		o.setCustomer(CustomerDAO.getCustomerById(cusDoc.getString("customer_id")));
		
		Document staffDoc = doc.get("staff", Document.class);
		o.setStaff(StaffDAO.getStaffById(staffDoc.getLong("staff_id")));
		
		Document addressDoc = doc.get("shipping_address",Document.class);
		Address	shipAddress = new Address();
		shipAddress.setCity(addressDoc.getString("city"));
		shipAddress.setState(addressDoc.getString("state"));
		shipAddress.setStreet(addressDoc.getString("street"));
		shipAddress.setZipCode(addressDoc.getInteger("zip_code"));
		
		o.setAddress(shipAddress);
		List<Document> odListDoc = doc.getList("order_details", Document.class);
		List<OrderDetail> odList = new ArrayList<>();
		for(Document d: odListDoc) {
			OrderDetail od = new OrderDetail();
			od.setColor(d.getString("color"));
			od.setQuantity(d.getInteger("quantity"));
			od.setPrice(d.getDouble("price"));
			od.setDiscount(d.getDouble("discount"));
			od.setProduct(ProductDAO.getProuctById(d.getLong("product_id")));
			odList.add(od);
		}
		o.setOrderDetails(odList);

		return o;
	}
	public static OrderStatus getOrderStatus(String str) {
		OrderStatus status = null;
		if(str.equalsIgnoreCase("new")) {
			status=OrderStatus.NEW;
		}
		if(str.equalsIgnoreCase("in_progress")) {
			status=OrderStatus.IN_PROGRESS;
		}
		if(str.equalsIgnoreCase("completed")) {
			status=OrderStatus.COMPLETED;
		}
		if(str.equalsIgnoreCase("partially_shipped")) {
			status=OrderStatus.PARTIALLY_SHIPPED;
		}
		if(str.equalsIgnoreCase("on_hold")) {
			status=OrderStatus.ON_HOLD;
		}
		if(str.equalsIgnoreCase("cancelled")) {
			status=OrderStatus.CANCELLED;
		}
		if(str.equalsIgnoreCase("awaiting_exchange")) {
			status=OrderStatus.AWAITING_EXCHANGE;
		}
		return status;
	}
	
	public static boolean insertOne(Order o) {
		AtomicReference<InsertOneResult> res = new AtomicReference<>();
		Publisher<InsertOneResult> pub = docCollection.insertOne(convert2Doc(o));
		SingleSub<InsertOneResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get().wasAcknowledged();
	}
	public static boolean insertMany(List<Order> list) {
		List<Document> listDoc = new ArrayList<>();
		for(Order o: list) {
			listDoc.add(convert2Doc(o));
		}
		AtomicReference<InsertManyResult> res = new AtomicReference<>();
		Publisher<InsertManyResult> pub = docCollection.insertMany(listDoc);
		SingleSub<InsertManyResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get().wasAcknowledged();
		
	}
	public static UpdateResult updateOne(Bson filter, Bson update) {
		return updateOne(filter, Arrays.asList(update));
	}
	public static UpdateResult updateOne(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		Publisher<UpdateResult> pub = docCollection.updateOne(filter, update);
		SingleSub<UpdateResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static UpdateResult updateMany(Bson filter, Bson update) {
		return updateMany(filter, Arrays.asList(update));
	}
	public static UpdateResult updateMany(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		Publisher<UpdateResult> pub = docCollection.updateMany(filter, update);
		SingleSub<UpdateResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static Document findOneAndUpdate(Bson filter, Bson update) {
		return findOneAndUpdate(filter, Arrays.asList(update));
	}
	public static Document findOneAndUpdate(Bson filter, List<Bson> update) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.findOneAndUpdate(filter, update);
		SingleSub<Document> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static DeleteResult deleteOne(Bson filter) {
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		Publisher<DeleteResult> pub = docCollection.deleteOne(filter);
		SingleSub<DeleteResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static DeleteResult deleteMany(Bson filter) {
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		Publisher<DeleteResult> pub = docCollection.deleteMany(filter);
		SingleSub<DeleteResult> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static Document findOneAndDelete(Bson filter) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.findOneAndDelete(filter);
		SingleSub<Document> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return sub.res.get();
	}
	public static Order getOrderById(ObjectId objectId) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.find(Filters.eq("_id",objectId)).first();
		SingleSub<Document> sub = new SingleSub<>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return convert2Order(sub.res.get());
	}
	public static List<Document> getDocsWithAggregate(List<Bson> bson){
		List<Document> list = new ArrayList<>();
		Publisher<Document> pub = docCollection.aggregate(bson);
		ListSub<Document> sub = new ListSub<>(list);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return list;
	}
}
