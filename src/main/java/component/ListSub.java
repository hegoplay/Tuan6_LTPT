package component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ListSub<T> implements Subscriber<T>{
	
	public List<T> list;
	public CountDownLatch latch;
	Subscription s;
	
	
	
	public ListSub(List<T> list) {
		this.list = list;
		this.latch = new CountDownLatch(1);
	}
	@Override
	public void onSubscribe(Subscription s) {
		// TODO Auto-generated method stub
		this.s = s;
		this.s.request(1);
	}
	@Override
	public void onNext(T t) {
		// TODO Auto-generated method stub
		list.add(t);
		s.request(1);
	}
	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		t.printStackTrace();
	}
	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		latch.countDown();
	}
	

}

