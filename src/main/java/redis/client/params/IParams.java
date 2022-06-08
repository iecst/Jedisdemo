package redis.client.params;

import redis.client.CommandArguments;

public interface IParams {

  void addParams(CommandArguments args);
}
