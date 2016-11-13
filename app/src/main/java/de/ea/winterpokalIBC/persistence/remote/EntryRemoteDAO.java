package de.ea.winterpokalIBC.persistence.remote;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.StringMap;
import com.google.gson.reflect.TypeToken;

import de.ea.winterpokalIBC.model.SportTypes;
import de.ea.winterpokalIBC.model.WPEntry;
import de.ea.winterpokalIBC.model.WPEntryAddToken;
import de.ea.winterpokalIBC.model.WPUser;
import de.ea.winterpokalIBC.model.WinterpokalException;
import de.ea.winterpokalIBC.persistence.IEntryDAO;
import de.ea.winterpokalIBC.utils.web.Constants;
import de.ea.winterpokalIBC.utils.web.RemoteRequest;
import de.ea.winterpokalIBC.utils.web.RequestType;

public class EntryRemoteDAO implements IEntryDAO {

	public void add(WPEntry entry) {

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").addSerializationExclusionStrategy(new ExclusionStrategy() {

			private List<String> allowedFields = Arrays.asList(new String[] { "category", "duration", "description", "date", "distance" });

			public boolean shouldSkipField(FieldAttributes f) {
				String fieldName = f.getName();
				return !allowedFields.contains(fieldName);
			}

			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		}).create();

		String data = gson.toJson(entry);

		String responseString = RemoteRequest.DoRequest(Constants.HOST_NAME + "entries/add.json", data, RequestType.POST);

		Type t = new TypeToken<Response<WPEntryAddToken>>() {}.getType();
		Response<WPEntryAddToken> response = gson.fromJson(responseString, t);

		if (response == null) {
			throw new DAORemoteException("NoResponse", null);
		} else if (!"OK".equalsIgnoreCase(response.getStatus())) {
			try {
				StringMap<String> errors = (StringMap<String>) response.getMessages();

				HashMap<String, String> errorMap = new HashMap<String, String>();
				if (errors != null)
					errorMap.putAll(errors);
				throw new DAORemoteException("ErrorAddEntry", errorMap);
			} catch (WinterpokalException e) {
				throw e;
			} catch (Exception ex) {

			}
			Log.d("EntryRemoteDAOResponse", responseString);
		}

	}

	public List<WPEntry> get(int limit) {
		return getEntries(Constants.HOST_NAME + "entries/my.json", limit);
	}

	public List<WPEntry> getForUser(int userId, int limit) {
		return getEntries(Constants.HOST_NAME + "entries/user/" + userId + ".json", limit);
	}

	public List<WPEntry> getForTeam(int teamId, int limit) {
		return getEntries(Constants.HOST_NAME + "entries/team/" + teamId + ".json", limit);
	}

	public List<WPEntry> getRecent(int limit) {
		return getEntries(Constants.HOST_NAME + "entries/recent.json", Math.min(200, limit));
	}

	private List<WPEntry> getEntries(String url, int limit) {
		HashMap<String, String> args = new HashMap<String, String>();
		if (limit > 0)
			args.put("limit", limit + "");
		String responseString = RemoteRequest.DoRequest(url, args, RequestType.GET);
		if(responseString == null) {
			return null;
		}
		Type t = new TypeToken<Response<List<EntryObject>>>() {}.getType();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Response<List<EntryObject>> response = gson.fromJson(responseString, t);

		List<WPEntry> entries = null;

		if (response != null && "OK".equals(response.getStatus())) {
			List<EntryObject> objList = response.getData();
			entries = new ArrayList<WPEntry>();
			for (EntryObject obj : objList) {
				WPEntry entry = obj.getEntry();
				entry.setCategory(obj.getCategory().getId());
				entry.setUser(obj.getUser());
				entries.add(entry);
			}
			Collections.sort(entries, new Comparator<WPEntry>() {

				public int compare(WPEntry lhs, WPEntry rhs) {
					return -lhs.getDate().compareTo(rhs.getDate());
				}
				
			});
			return entries;
		} else {
			Log.e("errorGetEntries", responseString);
		}
		return null;
	}

}

class EntryObject {
	private WPEntry entry;
	private WPUser user;
	private Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public WPEntry getEntry() {
		return entry;
	}

	public void setEntry(WPEntry entry) {
		this.entry = entry;
	}

	public WPUser getUser() {
		return user;
	}

	public void setUser(WPUser user) {
		this.user = user;
	}

}

class Category {
	private String title;
	private SportTypes id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SportTypes getId() {
		return id;
	}

	public void setId(SportTypes id) {
		this.id = id;
	}

}