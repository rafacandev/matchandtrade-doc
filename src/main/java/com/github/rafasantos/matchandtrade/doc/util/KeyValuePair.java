package com.github.rafasantos.matchandtrade.doc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

public class KeyValuePair<K, V> implements Map.Entry<K, V> {
	private final K key;
	private V value;

	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public static List<Entry<String, String>> buildEntryList(CloseableHttpResponse httpResponse) {
		Header[] responseHeaders = httpResponse.getAllHeaders();
		List<Entry<String, String>> responseHeadersList = new ArrayList<>();
		for (Header h : responseHeaders) {
			KeyValuePair<String, String> entry = new KeyValuePair<String, String>(h.getName(), h.getValue());
			responseHeadersList.add(entry);
		}
		return responseHeadersList;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValuePair<?, ?> other = (KeyValuePair<?, ?>) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

	@Override
	public String toString() {
		return key + ": " + value;
	}

}
