<template>
    <div style="height:100%">
        <iframe :src="url" width="100%" height="100%"></iframe>
    </div>
</template>

<script>
import CONST from '@/constants';
export default {
    data() {
        return {
            url: ''
        };
    },
    created() {
        // console.log(CONST);
        this.url = CONST.URL + '/ureport/designer';
    },
    methods: {}
};
</script>

<style lang="less" scoped>
</style>
