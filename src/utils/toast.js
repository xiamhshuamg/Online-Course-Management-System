import { reactive } from 'vue'

const state = reactive({
  items: []
})

function push(type, title, desc) {
  const id = Math.random().toString(16).slice(2)
  state.items.push({ id, type, title, desc })
  setTimeout(() => {
    const idx = state.items.findIndex(x => x.id === id)
    if (idx >= 0) state.items.splice(idx, 1)
  }, 2200)
}

export default {
  state,
  success(title, desc) { push('success', title, desc) },
  warning(title, desc) { push('warning', title, desc) },
  error(title, desc) { push('danger', title, desc) },
  info(title, desc) { push('info', title, desc) }
}

