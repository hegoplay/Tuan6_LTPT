package bai2.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import bai2.dao.ZipDAO;
import bai2.entities.Location;
import bai2.entities.Zip;
import utils.ConnectMongoDB;

public class Execute {
	public static void main(String[] args) {
		ConnectMongoDB.getConnection("sample_training");
		ZipDAO.getCollection();
//		AggregatePublisher<Product> pub = productCollection
//				.aggregate(Arrays.asList(
//						Aggregates.group(null, Accumulators.max("maxPrice", "$price"),
//								Accumulators.addToSet("ps", "$$ROOT")),
//						Aggregates.unwind("$ps"),
//						Aggregates.match(Filters.expr(Filters.eq("$eq", Arrays.asList("$ps.price", "$maxPrice")))),
//						Aggregates.replaceWith("$ps")
//
//				));
		
//		cau1();
//		cau2();
		cau3();
		ConnectMongoDB.closeClient();
	}
//	1. Hiển thị n documents từ document thứ k
	private static void cau1() {
		int k = 1;
		int n = 5;
		List<Zip> res = ZipDAO.getZipsAggregate(Arrays.asList(
				Aggregates.skip(k-1),
				Aggregates.limit(n)));
		for(Zip r : res) {
			System.out.println(r);
		}
	}
//	2. Chèn thêm 1 document mới
	private static void cau2() {
		Zip newZip = new Zip(ObjectId.get(), "HCMC","12923", new Location(-65.23f,-42.32f),20, "VN" );
		System.out.println(ZipDAO.insertZip(newZip));
	}
//	3. Cập nhật thông tin của một document khi biết id
	private static void cau3() {
		Zip updateZip = new Zip(new ObjectId("65f2b488f54e784530428b52"), "HCMC","71000", new Location(-65.23f,-42.32f),20, "VN" );
		ZipDAO.updateZip(updateZip);
	}
//	4. Tìm các document có city là PALMER
	private static void cau4() {
		List<Zip> zips = ZipDAO.getZipsFindConditions(Filters.eq("city","PALMER"));
		for(Zip z : zips) {
			System.out.println(z);
		}
	}
//	5. Tìm các document có dân số >100000
	private static void cau5() {
		List<Zip> zips = ZipDAO.getZipsFindConditions(Filters.gt("pop", 100000));
		for(Zip z : zips) {
			System.out.println(z);
		}
	}
//	6. Tìm dân số của thành phố FISHERS ISLAND
	private static void cau6() {
		List<Zip> zips = ZipDAO.getZipsFindConditions(Filters.eq("city", "FISHERS ISLAND"));
		for(Zip z : zips) {
			System.out.println("Dan so FISHERS ISLAND = " + z.getPop());
		}
	}
//	7. Tìm các thành phố có dân số từ 10 – 50
	private static void cau7() {
		List<Zip> zips = ZipDAO.getZipsFindConditions(
				Filters.and(
					Filters.gte("pop",10),
					Filters.lte("pop",50)));
		for(Zip z : zips) {
			System.out.println(z);
		}
	}
//	8. Tìm tất cả các thành phố của bang MA có dân số trên 500
	private static void cau8() {
		List<Zip> zips = ZipDAO.getZipsFindConditions(
				Filters.and(
					Filters.eq("state","MA"),
					Filters.gt("pop",500)));
		Set<String> s = new HashSet<>();
		for(Zip z : zips) {
			s.add(z.getCity());
		}
		for (String str : s) {
			System.out.println(str);
		}
	}
//	9. Tìm tất cả các bang (không trùng)
	private static void cau9() {
		List<Document> list = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$state",Accumulators.sum("count", 1))));
		for(Document doc : list) {
			System.out.println(doc.get("_id"));
		}
	}
//	10.Tìm tất cả các bang mà có chứa ít nhất 1 thành phố có dân số trên 100000
	private static void cau10()
	{
//		db.zips.aggregate([{$group:{_id:"$state",pop:{$addToSet:"$pop"}}},{$match:{$expr:{$gt:["$pop",100000]}}},{$group:{_id:null,count:{$sum:1}}}])
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.match(
					Filters.gt("pop", 100000)),
				Aggregates.group("$state", Accumulators.sum("count", 1))));
		for(Document d: doc) {
			System.out.println(d.get("_id"));
		}
	}
//	11.Tính dân số trung bình của mỗi bang
	private static void cau11()
	{
//		db.zips.aggregate([{$group:{_id:"$state",pop:{$addToSet:"$pop"}}},{$match:{$expr:{$gt:["$pop",100000]}}},{$group:{_id:null,count:{$sum:1}}}])
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$state", Accumulators.avg("avg_pop", "$pop"))));
		for(Document d: doc) {
			if (d.get("avg_pop")!=null)
				System.out.println(d.get("_id") + " = "+ d.get("avg_pop"));
		}
	}
//	12.Tìm những document của bang 'CT' và thành phố 'WATERBURY'
	private static void cau12()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.match(
					Filters.and(
							Filters.eq("state","CT"),
							Filters.eq("city","WATERBURY")))));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	13.Bang WA có bao nhiêu city (nếu trùng chỉ tính 1 lần)
	private static void cau13()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.match(Filters.eq("state","WA")),
				Aggregates.group("$state",Accumulators.addToSet("city", "$city")),
				Aggregates.project(Projections.fields(
					Projections.computed(
						"cityCnt", 
						Projections.computed(
							"$size", 
							"$city"))))));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	14.Tính số city của mỗi bang (nếu trùng chỉ tính 1 lần), kết quả giảm dần theo số city
	private static void cau14()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$state",Accumulators.addToSet("city", "$city")),
				Aggregates.project(Projections.fields(
					Projections.computed(
						"cityCnt", 
						Projections.computed(
							"$size", 
							"$city")))),
				Aggregates.sort(Sorts.descending("cityCnt"))));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	15.Tìm tất cả các bang có tổng dân số trên 10000000
	private static void cau15()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$state",Accumulators.sum("pop", "$pop")),
				Aggregates.match(
					Filters.gt("pop", 10000000)
						)));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	16.Tìm các document có dân số lớn (nhỏ) nhất
	private static void cau16()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group(
					null,
					Accumulators.addToSet("val", "$$ROOT"),
					Accumulators.max("maxPop", "$pop")),
				Aggregates.unwind("$val"),
				Aggregates.match(Filters.expr(Document.parse("{$eq:[\"$val.pop\",\"$maxPop\"]}"))),
				Aggregates.replaceRoot("$val")));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	17.Tìm bang có tổng dân số lớn nhất
	private static void cau17()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group(
					"$state",
					Accumulators.sum("pop", "$pop")),
				Aggregates.group(
						null,
						Accumulators.addToSet("val", "$$ROOT"),
						Accumulators.max("maxPop", "$pop")),
				Aggregates.unwind("$val"),
				Aggregates.match(Filters.expr(Document.parse("{$eq:[\"$val.pop\",\"$maxPop\"]}"))),
				Aggregates.replaceRoot("$val")));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	18.Xuất những document có dân số dưới dân số trung bình của mỗi city
	private static void cau18()
	{
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$city", Accumulators.sum("pop","$pop")),
				Aggregates.group(
					null,
					Accumulators.addToSet("val", "$$ROOT"),
					Accumulators.avg("avgPop", "$pop")),
				Aggregates.unwind("$val"),
				Aggregates.match(Filters.expr(Document.parse("{$lt:[\"$val.pop\",\"$avgPop\"]}"))),
				Aggregates.replaceRoot("$val")));
		for(Document d: doc) {
			System.out.println(d.toJson());
		}
	}
//	19.Dựa vào tập kết quả câu trên, xóa các documents khi biết city
	private static void cau19() {
//		AVOID RUNNING THIS
		List<Document> doc = ZipDAO.getDocsAggregate(Arrays.asList(
				Aggregates.group("$city", Accumulators.sum("pop","$pop")),
				Aggregates.group(
					null,
					Accumulators.addToSet("val", "$$ROOT"),
					Accumulators.avg("avgPop", "$pop")),
				Aggregates.unwind("$val"),
				Aggregates.match(Filters.expr(Document.parse("{$lt:[\"$val.pop\",\"$avgPop\"]}"))),
				Aggregates.replaceRoot("$val")));
		System.out.println(ZipDAO.removeDocument(doc,"city","_id")); 
//		Document d = new Document();
//		d.append("_id", "HADLEY");
//		ZipPOJODAO.removeDocument(Arrays.asList(d),"city","_id");
	}
}
