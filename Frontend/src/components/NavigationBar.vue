<template>
  <v-container>
    <v-app-bar app color="indigo" dark>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>BusFlow</v-toolbar-title>
    </v-app-bar>
  </v-container>
</template>

<script>
export default {
  name:"NavigationBar",
  data: () => ({})
};
</script>

<style scoped>
.toolbar {
  width: 100%;
}
</style>
