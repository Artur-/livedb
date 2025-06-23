import { useSignal } from "@vaadin/hilla-react-signals";
import { Grid, GridColumn } from "@vaadin/react-components";
import { ItemService } from "Frontend/generated/endpoints";
import Item from "Frontend/generated/org/vaadin/artur/hilla/livedb/Item";
import Operation from "Frontend/generated/org/vaadin/artur/hilla/livedb/ItemEvent/Operation";
import { useEffect } from "react";

export default function Main() {
  const items = useSignal<Item[]>([]);
  useEffect(() => {
    ItemService.getItems().onNext((value) => {
      items.value = [...items.value, value];
    });
    ItemService.getItemUpdates().onNext((value) => {
      switch (value.operation) {
        case Operation.ADD:
          items.value = [...items.value, value.item!];
          break;
        case Operation.DELETE:
          items.value = items.value.filter((item) => item.id !== value.id);
          break;
        case Operation.UPDATE:
          items.value = items.value.map((item) => (item.id === value.id ? value.item! : item));
      }
    });
  }, []);

  return (
    <Grid style={{height: "100vh"}} items={items.value}>
      <GridColumn path="id"></GridColumn>
      <GridColumn path="quantity"></GridColumn>
      <GridColumn path="name"></GridColumn>
    </Grid>
  );
}
