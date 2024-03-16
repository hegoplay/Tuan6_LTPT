package tuan5.run;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.reactivestreams.Publisher;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import tuan5.dao.MovieDAO;
import utils.ConnectMongoDB;

public class Execute {
	public static void main(String[] args) {
		ConnectMongoDB.getConnection("sample_mflix");
		MovieDAO.getCollections();
//		cau1();
//		cau2();
//		cau3();
//		cau4();
//		cau5();
//		cau6();
//		cau7();
//		cau8();
//		cau9();
		cau10();
		ConnectMongoDB.closeClient();
	}

	private static void cau1() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.group("$directors",
						Accumulators.sum("number of movies", 1)),
				Aggregates.match(Filters.expr(new Document("$gte",Arrays.asList(
						"$number of movies",30)))),
				
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("director", "$_id"),
								Projections.include("number of movies")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau2() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.group("$directors",
						Accumulators.sum("num", 1)),
				Aggregates.group(null, 
						Accumulators.addToSet("rot", "$$ROOT"),
						Accumulators.max("max", "$num")),
				Aggregates.unwind("$rot"),
				Aggregates.match(Filters.expr(Filters.eq("$eq", Arrays.asList(
						"$rot.num","$max")))),
				Aggregates.replaceRoot("$rot"),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("director", "$_id")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau3() {
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.group("$year",
						Accumulators.addToSet("movies", "$title")),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("director", "$_id"),
								Projections.include("movies")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
		
	}
	private static void cau4() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.group("$year",
						Accumulators.sum("number of movies", 1)),
				Aggregates.sort(Sorts.descending("number of movies")),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("year", "$_id"),
								Projections.include("number of movies")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau5() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.group("$year",
						Accumulators.sum("number of movies", 1)),
				Aggregates.sort(Sorts.descending("number of movies")),
				Aggregates.limit(1),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("year", "$_id")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau6() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$countries"),
				Aggregates.group("$countries",
						Accumulators.addToSet("movies", "$title")),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("country", "$_id"),
								Projections.include("movies")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau7() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$countries"),
				Aggregates.group("$countries",
						Accumulators.sum("number of movies", 1)),
				Aggregates.sort(Sorts.descending("number of movies")),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.computed("country", "$_id"),
								Projections.include("number of movies")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
	private static void cau8() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.match(
						Filters.expr(
								new Document("$and", Arrays.asList(
										new Document("$eq", Arrays.asList(
												new Document("$year", "$released"),
												2016)),
										new Document("$eq",Arrays.asList(
												new Document("$month","$released"),
												3)))))),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.include("title")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
//	. Liệt kê những tựa phim (title) do diễn viên “Frank Powell” hoặc “Charles Wellesley” đóng
	private static void cau9() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$directors"),
				Aggregates.match(
						Filters.expr(
								new Document("$or", Arrays.asList(
										new Document("$eq", Arrays.asList(
												"$directors",
												"Frank Powell")),
										new Document("$eq",Arrays.asList(
												"$directors",
												"Charles Wellesley")))))),
				Aggregates.project(
						Projections.fields(
								Projections.excludeId(),
								Projections.include("title")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
//	10.Tìm những quốc gia phát hành nhiều bộ phim nhất 
	private static void cau10() {
		// TODO Auto-generated method stub
		List<Document> list = MovieDAO.getDocsWithAggregate(Arrays.asList(
				Aggregates.unwind("$countries"),
				Aggregates.group("$countries", Accumulators.sum("count", 1)),
				Aggregates.group(null, Accumulators.addToSet("rot", "$$ROOT"),Accumulators.max("max", "$count")),
				Aggregates.unwind("$rot"),
				Aggregates.match(Filters.expr(new Document("$eq",Arrays.asList("$rot.count","$max")))),
				Aggregates.replaceRoot("$rot"),
				Aggregates.project(Projections.fields(
						Projections.excludeId(),
						Projections.computed("country", "$_id")))));
		
		for(Document d : list) {
			System.out.println(d);
		}
	}
}
