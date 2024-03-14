package bai4.dao;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SingleSub<T> implements Subscriber<T>{
	
	public AtomicReference<T> res;
	public CountDownLatch latch;
	
	
	
	public SingleSub(AtomicReference<T> res) {
		this.res = res;
		this.latch = new CountDownLatch(1);
	}
	@Override
	public void onSubscribe(Subscription s) {
		// TODO Auto-generated method stub
		s.request(1);
	}
	@Override
	public void onNext(T t) {
		// TODO Auto-generated method stub
		res.set(t);
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
